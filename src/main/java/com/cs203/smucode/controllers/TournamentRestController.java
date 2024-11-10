package com.cs203.smucode.controllers;

import com.cs203.smucode.constants.MediaConstants;
import com.cs203.smucode.constants.OAuth2Constants;
import com.cs203.smucode.constants.UserRole;
import com.cs203.smucode.consumers.UserServiceConsumer;
import com.cs203.smucode.dtos.tournaments.*;
import com.cs203.smucode.dtos.users.TournamentParticipantsDTO;
import com.cs203.smucode.exceptions.ImageUploadUnsucessfulException;
import com.cs203.smucode.exceptions.InvalidTokenException;
import com.cs203.smucode.exceptions.UnauthorizedResourceAccessException;
import com.cs203.smucode.mappers.TournamentMapper;
import com.cs203.smucode.models.Tournament;
import com.cs203.smucode.services.TournamentService;
import com.cs203.smucode.utils.AWSUtil;
import com.cs203.smucode.utils.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/tournaments")
public class TournamentRestController {
    private static final Logger logger = LoggerFactory.getLogger(TournamentRestController.class);

    private final TournamentService tournamentService;
    private final TournamentMapper tournamentMapper;
    private final UserServiceConsumer userServiceConsumer;
    private final AWSUtil awsUtil;

    @Autowired
    public TournamentRestController(TournamentService tournamentService,
                                    TournamentMapper tournamentMapper,
                                    UserServiceConsumer userServiceConsumer,
                                    AWSUtil awsUtil) {
        this.tournamentService = tournamentService;
        this.tournamentMapper = tournamentMapper;
        this.userServiceConsumer = userServiceConsumer;
        this.awsUtil = awsUtil;
    }

    /**
     * Retrieves all tournaments relevant to the authenticated user.
     * - Admin users see tournaments they created.
     * - Regular users see tournaments they are registered or participating in.
     *
     * @return a list of TournamentCardDTO objects (AdminTournamentCardDTOs for admins and UserTournamentCardDTOs for users)
     */
    @Operation(summary = "Get all of user's tournaments")
    @GetMapping()
    public List<? extends TournamentCardDTO> getAllTournaments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = JWTUtil.getClaim(authentication, OAuth2Constants.SCOPE);
        String username = JWTUtil.getClaim(authentication, OAuth2Constants.SUBJECT);

        if (role == null || username == null) {
            throw new UnauthorizedResourceAccessException("User information could not be retrieved.");
        }

        // Admin - retrieve tournaments that they created
        if (UserRole.ADMIN.getAuthority().equals(role)) {
            List<Tournament> tournaments = tournamentService.findAllTournamentsByOrganiser(username);
            return tournamentMapper.tournamentsToAdminTournamentCardDTOs(tournaments);
        }

        // User - retrieve tournaments that they are signed up / participating in
        List<Tournament> tournaments = tournamentService.findAllTournamentsByRegistrant(username);
        return tournamentMapper.tournamentsToUserTournamentCardDTOs(tournaments, username);
    }

    /**
     * Get eligible tournaments for user to explore.
     * Retrieves tournaments that the user has not signed up for and that still have signups open.
     *
     * @return a list of UserTournamentCardDTO representing eligible tournaments for the user
     */
    @Operation(summary = "Get eligible tournaments for user")
    @GetMapping("/explore")
    public List<UserTournamentCardDTO> getAllEligibleTournaments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = JWTUtil.getClaim(authentication, OAuth2Constants.SUBJECT);

        List<Tournament> eligibleTournaments = tournamentService.findAllEligibleTournamentsForUser(username);
        return tournamentMapper.tournamentsToUserTournamentCardDTOs(eligibleTournaments, username);
    }

    /**
     * Get tournament overview by tournament ID.
     *
     * - **Admin view**: Provides access to the tournament details including a 'band' field.
     * - **User view**: Shows user-specific information, including fields like 'signedUp' and 'participated',
     * indicating the user's involvement in the tournament.
     *
     * @param tournamentId the unique identifier of the tournament
     * @return TournamentDTO, either AdminTournamentDTO or UserTournamentDTO based on the user's role
     */
    @Operation(summary = "Get tournament overview by tournament ID")
    @GetMapping("/{tournamentId}")
    public TournamentDTO getTournamentById(@PathVariable UUID tournamentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = JWTUtil.getClaim(authentication, OAuth2Constants.SCOPE);
        String username = JWTUtil.getClaim(authentication, OAuth2Constants.SUBJECT);

        Tournament tournament = tournamentService.findTournamentById(tournamentId);

        // Return Admin view if user is an admin
        if (UserRole.ADMIN.getAuthority().equals(role)) {
            return tournamentMapper.tournamentToAdminTournamentDTO(tournament);
        }

        // Return User view
        return tournamentMapper.tournamentToUserTournamentDTO(tournament, username);
    }

    @Operation(summary = "Get tournament brackets by tournament ID")
    @GetMapping("/{tournamentId}/brackets")
    public TournamentBracketsDTO getTournamentBracketsByTournamentId(@PathVariable UUID tournamentId) {
        Tournament tournament = tournamentService.findTournamentById(tournamentId);
        return tournamentMapper.tournamentToTournamentBracketsDTO(tournament);
    }

    @GetMapping("/{tournamentId}/participants")
    public TournamentParticipantsDTO getTournamentParticipants(@PathVariable UUID tournamentId) {
        Tournament tournament = tournamentService.findTournamentById(tournamentId);
        return tournamentMapper.tournamentToTournamentParticipantsDTO(tournament, userServiceConsumer);
    }

    @Operation(summary = "Create new tournament")
    @PostMapping("/create")
    public DetailedTournamentDTO createTournament(@Valid @RequestBody DetailedTournamentDTO tournamentDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = JWTUtil.getClaim(authentication, OAuth2Constants.SUBJECT);

        Tournament tournamentToCreate = tournamentMapper.detailedTournamentDTOToTournament(tournamentDTO);
        tournamentToCreate.setOrganiser(username); // Set organiser as admin who submitted request
        Tournament createdTournament = tournamentService.createTournament(tournamentToCreate);
        logger.info("Created tournament: {}", createdTournament);
        return tournamentMapper.tournamentToDetailedTournamentDTO(createdTournament);
    }

    @Operation(summary = "Update tournament by tournament ID")
    @PutMapping("/{tournamentId}")
    public DetailedTournamentDTO updateTournament(@PathVariable UUID tournamentId,
                                                  @Valid @RequestBody DetailedTournamentDTO tournamentDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = JWTUtil.getClaim(authentication, OAuth2Constants.SUBJECT);

        // Authorisation check: only organiser should be able to update tournament
        if (!username.equals(tournamentService.findTournamentById(tournamentId).getOrganiser())) {
            throw new UnauthorizedResourceAccessException(
                    String.format("User %s is not authorized to update tournament %s", username, tournamentId)
            );
        }

        Tournament tournament = tournamentMapper.detailedTournamentDTOToTournament(tournamentDTO);
        tournamentService.updateTournament(tournamentId, tournament);
        return tournamentDTO;
    }

    @Operation(summary = "Create new tournament sign up for user")
    @PostMapping("/{tournamentId}/signup")
    public DetailedTournamentDTO addTournamentSignups(@PathVariable UUID tournamentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = JWTUtil.getClaim(authentication, OAuth2Constants.SUBJECT);

        Tournament tournament = tournamentService.findTournamentById(tournamentId);
        tournamentService.addTournamentSignup(tournamentId, username);
        return tournamentMapper.tournamentToDetailedTournamentDTO(tournament);
    }

    @Operation(summary = "Delete existing tournament sign up for user")
    @DeleteMapping("/{tournamentId}/signup")
    public DetailedTournamentDTO deleteTournamentSignups(@PathVariable UUID tournamentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = JWTUtil.getClaim(authentication, OAuth2Constants.SUBJECT);

        Tournament tournament = tournamentService.findTournamentById(tournamentId);
        tournamentService.deleteTournamentSignup(tournamentId, username);
        return tournamentMapper.tournamentToDetailedTournamentDTO(tournament);
    }

    @PatchMapping("/{tournamentId}/leave")
    public DetailedTournamentDTO leaveTournament(@PathVariable UUID tournamentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = JWTUtil.getClaim(authentication, OAuth2Constants.SUBJECT);

        Tournament tournament = tournamentService.findTournamentById(tournamentId);
        tournamentService.deleteTournamentParticipant(tournamentId, username);
        return tournamentMapper.tournamentToDetailedTournamentDTO(tournament);
    }

    @Operation(summary = "End current round and populate next round brackets")
    @PutMapping("/rounds/{roundId}/end")
    public TournamentDTO endRound(@PathVariable UUID roundId) {
        Tournament tournament = tournamentService.progressTournamentToNextRound(roundId);
        return tournamentMapper.tournamentToAdminTournamentDTO(tournament);
    }

    @Operation(summary = "Delete existing tournament by tournament ID")
    @DeleteMapping("/{tournamentId}")
    public void deleteTournamentById(@PathVariable UUID tournamentId) { tournamentService.deleteTournamentById(tournamentId); }

    @Operation(summary = "Generate presigned link to upload an image to s3")
    @PostMapping("/get-upload-link")
    public ResponseEntity<UploadLinkResponseDTO> getPreSignedUrl(
            @RequestParam UUID tournamentId,
            @RequestParam String contentType
    ) {

        if (tournamentId == null) {
            throw new ImageUploadUnsucessfulException("Invalid tournamentId");
        }

        if (contentType == null || contentType.isEmpty()) {
            throw new ImageUploadUnsucessfulException("Content type is mandatory");
        }

        if (!MediaConstants.SUPPORTED_MEDIA.contains(contentType)) {
            throw new ImageUploadUnsucessfulException("Unsupported content type: " + contentType);
        }

        Tournament tournament = tournamentService.findTournamentById(tournamentId);

        try {
            String preSignedUrl = awsUtil.generatePresignedUrl(tournament.getId(), contentType);
            String key = awsUtil.getKey(tournament.getId());

            return ResponseEntity.ok(
                    new UploadLinkResponseDTO(key, preSignedUrl)
            );
        } catch (Exception e) {
            throw new ImageUploadUnsucessfulException("An error occurred while uploading the profile picture");
        }
    }

    @Operation(summary = "Persist image to database upon successful upload")
    @PostMapping("/upload-picture")
    public ResponseEntity<UploadSuccessResponseDTO> uploadPicture(@RequestParam UUID tournamentId,
                                                                  @RequestParam String key) {

        if (key == null || key.isEmpty()) {
            throw new ImageUploadUnsucessfulException("Please provide a non-empty or null key");
        }

        if (!key.startsWith("tournament-pictures/")) {
            throw new ImageUploadUnsucessfulException("Invalid key: " + key + ", please provide a valid key");
        }

        if (!awsUtil.getKey(tournamentId).equals(key)) {
            throw new ImageUploadUnsucessfulException("Input key does not match generated key");
        }

        Tournament tournament = tournamentService.findTournamentById(tournamentId);
        try {
            String imageUrl = awsUtil.getObjectUrl(tournament.getId());
            tournamentService.uploadTournamentPicture(tournament, imageUrl);

            return ResponseEntity.ok(
                    new UploadSuccessResponseDTO("success", imageUrl)
            );
        } catch (Exception e) {
            throw new ImageUploadUnsucessfulException("An error occurred while uploading the profile picture");
        }
    }
}

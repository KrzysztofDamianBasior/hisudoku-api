package org.hisudoku.hisudokuapi.users.services;

import lombok.RequiredArgsConstructor;

import org.hisudoku.hisudokuapi.general.exceptions.*;
import org.hisudoku.hisudokuapi.sudokus.repositories.SudokuComplexQueriesRepository;
import org.hisudoku.hisudokuapi.users.dtos.ActivateEmailInput;
import org.hisudoku.hisudokuapi.users.dtos.ForgotPasswordInput;
import org.hisudoku.hisudokuapi.users.dtos.UpdateMyEmailInput;
import org.hisudoku.hisudokuapi.users.dtos.UpdateMyUsernameInput;
import org.hisudoku.hisudokuapi.users.entities.EmailActivationToken;
import org.hisudoku.hisudokuapi.users.entities.HSUser;
import org.hisudoku.hisudokuapi.users.models.AccountModel;
import org.hisudoku.hisudokuapi.users.models.HSUserPrincipal;
import org.hisudoku.hisudokuapi.users.models.MessageResponseModel;
import org.hisudoku.hisudokuapi.users.repositories.EmailActivationTokenComplexQueriesRepository;
import org.hisudoku.hisudokuapi.users.repositories.HSUserComplexQueriesRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserActionsService {
    private final HSUserComplexQueriesRepository userRepository;
    private final AuthenticationService authenticationService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final EmailActivationTokenComplexQueriesRepository emailActivationTokenComplexQueriesRepository;
    private final SudokuComplexQueriesRepository sudokuComplexQueriesRepository;

    public MessageResponseModel requestUpdatePasswordByOTT(ForgotPasswordInput forgotPasswordInput, String lang) {
        String email = forgotPasswordInput.getEmail();
        HSUser hsUser = userRepository.findOneByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(UserNotFoundException.ByProperty.EMAIL, email));

        mailService.sendResetPasswordByOTT(email, hsUser.getName(), authenticationService.createOttLink(hsUser.getName(), "/account/reset-password"), lang);
        //  updateResetPasswordLink(newResetPasswordLink, userId)
        //  findUserByResetPasswordLink
        //  updatePasswordAndRemoveResetPasswordLink(resetPasswordLink, newPassword)
        return new MessageResponseModel("a link to reset your account password has been sent to your email address");
    }

    public MessageResponseModel requestUpdatePasswordByJWT(ForgotPasswordInput forgotPasswordInput, String lang) {
        String email = forgotPasswordInput.getEmail();
        HSUser hsUser = userRepository.findOneByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(UserNotFoundException.ByProperty.EMAIL, email));

        String token = jwtService.issueAuthToken(new HSUserPrincipal(hsUser));

        // we can insert jwt token in both using the headers or query parameters of URL
        // JWT tokens are URL-safe when it comes to their syntax- a JWT is represented as a sequence of URL-safe parts separated by period (.) characters. Each part contains a base64url-encoded value.
        // However browsers, web servers, and other software may not adequately secure URLs in the browser history, web server logs, and other data structures. If bearer tokens are passed in page URLs, attackers might be able to steal them from the history data, logs, or other unsecured locations.
        mailService.sendResetPasswordByJWT(email, hsUser.getName(), token, lang);
        //  updateResetPasswordLink(newResetPasswordLink, userId)
        //  findUserByResetPasswordLink
        //  updatePasswordAndRemoveResetPasswordLink(resetPasswordLink, newPassword)
        return new MessageResponseModel("a link to reset your account password has been sent to your email address");
    }

    public AccountModel updatePassword(HSUserPrincipal principal, String newPassword) {
        return userRepository.findOneByIdUpdatePassword(principal.getId(), passwordEncoder.encode(newPassword))
                .map(HSUserUtils::mapToAccountModelCreatedSudokusNullDTO)
                .orElseThrow(() -> new OperationFailedException("change password"));
//        Optional.ofNullable(context.getAuthentication())
//                .ifPresentOrElse(authentication -> {
//                    HSUserPrincipal principal = (HSUserPrincipal) authentication.getPrincipal();
//                    userRepository.findOneByIdUpdatePassword(principal.getId(), passwordEncoder.encode(newPassword))
//                            .orElseThrow(() -> new OperationFailedException("change password"));
//                }, () -> {
//                    throw new IllegalStateException("User not logged in!");
//                });
    }

    public MessageResponseModel requestUpdateEmail(HSUserPrincipal principal, UpdateMyEmailInput updateMyEmailInput, String lang) {
        String newEmail = updateMyEmailInput.getNewEmail();
        if (userRepository.doesEmailExist(newEmail)) {
            throw new EmailTakenException(newEmail);
        }
        EmailActivationToken emailActivationToken = emailActivationTokenComplexQueriesRepository.addOne(newEmail, principal.getId())
                .orElseThrow(() -> new OperationFailedException("request update email"));

        mailService.sendActivateEmailByUUID(newEmail, principal.getName(), emailActivationToken.getToken(), lang);

        return new MessageResponseModel("a link to activate your email address has been sent to the address provided");
    }

    public AccountModel activateEmail(ActivateEmailInput activateEmailInput) {
        String token = activateEmailInput.getToken();
        EmailActivationToken emailActivationToken = emailActivationTokenComplexQueriesRepository.findOneByToken(token)
                .orElseThrow(() -> new EmailActivationTokenNotFound(token));
        if (userRepository.doesEmailExist(emailActivationToken.getEmail())) {
            throw new EmailTakenException(emailActivationToken.getEmail());
        }
        HSUser user = userRepository.findOneByIdUpdateEmail(emailActivationToken.getPrincipalId(), emailActivationToken.getEmail())
                .orElseThrow(() -> new OperationFailedException("activate email"));
        emailActivationTokenComplexQueriesRepository.removeOneById(emailActivationToken.getId())
                .orElseThrow(() -> new OperationFailedException("activate email"));

        return HSUserUtils.mapToAccountModelCreatedSudokusNullDTO(user);
    }

    public AccountModel findOneById(String id) {
        return userRepository.findOneById(id)
                .map(HSUserUtils::mapToAccountModelCreatedSudokusNullDTO)
                .orElseThrow(() -> new UserNotFoundException(UserNotFoundException.ByProperty.ID, id));
        // repository.findById(id).map(Utils::mapToDTO).orElseThrow(() -> new NotFoundException(id));
    }

    public AccountModel findOneByName(String name) {
        return userRepository.findOneByName(name)
                .map(HSUserUtils::mapToAccountModelCreatedSudokusNullDTO)
                .orElseThrow(() -> new UserNotFoundException(UserNotFoundException.ByProperty.NAME, name));
    }

    public AccountModel updateOneUsername(HSUserPrincipal principal, UpdateMyUsernameInput updateMyUsernameInput) {
        String userId = principal.getId();
        String newUsername = updateMyUsernameInput.getNewUsername();

        if (this.userRepository.doesNameExist(newUsername)) {
            throw new NameTakenException(newUsername);
        }

        return this.userRepository.updateOneUsername(userId, newUsername)
                .map(HSUserUtils::mapToAccountModelCreatedSudokusNullDTO)
                .orElseThrow(() -> new OperationFailedException("update username"));
    }

    public MessageResponseModel removeOne(HSUserPrincipal principal) {
        this.sudokuComplexQueriesRepository.removeManyByAuthor(principal.getId());
        HSUser hsUser = this.userRepository.removeOneById(principal.getId())
                .orElseThrow(() -> new OperationFailedException("remove one"));
        return new MessageResponseModel("removed user with id: " + hsUser.getId());
    }

//    public UserModel findUsersWithTheirSudokus(String id) {
//        Page<User> userPage = userRepository.findAll(pageable);
//        List<User> users = userPage.getContent();
//        List<Long> ids = users.stream()
//                .map(Sudoku::getId)
//                .collect(Collectors.toList());
//        List<Sudoku> sudokus = sudokuRepository.findAllByAuthorIdIn(ids);
//        users.forEach(user -> user.setSudokus(
//                        sudokus.stream()
//                                .filter(sudoku -> sudoku.getAuthor().getId().equals(user.getId()))
//                                .collect(Collectors.toList())
//                )
//        );
//        return users;
//    }
}

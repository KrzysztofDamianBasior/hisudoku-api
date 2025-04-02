package org.hisudoku.hisudokuapi.users.services;

import lombok.RequiredArgsConstructor;

import org.hisudoku.hisudokuapi.users.entities.HSUser;
import org.hisudoku.hisudokuapi.users.models.HSUserPrincipal;
import org.hisudoku.hisudokuapi.users.repositories.HSUserComplexQueriesRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class HSUserDetailsService implements UserDetailsService {
    // implements UserDetailsManager, UserDetailsPasswordService

    private final HSUserComplexQueriesRepository userRepository;

    // private static Logger LOGGER = LoggerFactory.getLogger(HSUserDetailsManager.class);
    // private final PasswordEncoder passwordEncoder;  // circular dependency with security config

    // private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
//    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
//        Assert.notNull(securityContextHolderStrategy, "securityContextHolderStrategy cannot be null");
//        this.securityContextHolderStrategy = securityContextHolderStrategy;
//    }

    @Override
    public UserDetails loadUserByUsername(String name) {
        // UsernameNotFoundException which is a subclass of AuthenticationException can be thrown during authentication
        HSUser user = userRepository.findOneByName(name)
                .orElseThrow(() -> new UsernameNotFoundException(name));
        return new HSUserPrincipal(user);

        // without custom principal:
        // import org.springframework.security.core.userdetails.User;
        // return new User(user.getUsername(), user.getPassword(), user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList()));
    }

    public UserDetails loadUserById(String id) {
        HSUser user = userRepository.findOneById(id)
                .orElseThrow(() -> new UsernameNotFoundException("user with id: " + id));
        return new HSUserPrincipal(user);
    }

//    @Override
//    public void createUser(UserDetails user) {
//        Assert.isTrue(!userExists(user.getUsername()), "user should not exist");
//        //     public Optional<HSUser> addOne(String name, String hashedPassword, String role)
//        //     public Optional<HSUser> addOne(String name, String hashedPassword, String role, String email)
//        userRepository.addOne(user.getUsername(), user.getPassword(), Role.USER.name())
//                .orElseThrow(() -> new OperationFailedException("create user"));
//    }

//    @Override
//    public void updateUser(UserDetails user) {
//        Assert.isTrue(userExists(user.getUsername()), "user should exist");
//        // not implemented
//    }

//    @Override
//    public void deleteUser(String username) {
//        userRepository.removeOneByName(username);
//    }

//    @Override
//    public void changePassword(String oldPassword, String newPassword) {
//        HSUserPrincipal currentUser = (HSUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        //HSUserPrincipal currentUser = (HSUserPrincipal) this.securityContextHolderStrategy.getContext().getAuthentication().getPrincipal();
//
//        if (currentUser == null) {
//            // This would indicate bad coding somewhere
//            throw new AccessDeniedException("Can't change password as no Authentication object found in context for current user.");
//        }
//        String username = currentUser.getName();

        // logger.debug("Changing password for user {}.", username);
        // %[argument_index$][flags][width][.precision]conversion
        // String multipleFormat = String.format("Boolean: %b, Character: %c, Decimal: %d, Hex: %x, Float: %.2f, Exponential: %e, String: %s",boolValue, charValue, intValue, intValue, floatValue, floatValue, stringValue);

        // If an authentication manager has been set, re-authenticate the user with the supplied password.
//        if (this.authenticationManager != null) {
//            LOGGER.debug("Reauthenticating user '%s' for password change request.", username);
//            this.authenticationManager
//                    .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, oldPassword));
//        } else {
//			LOGGER.debug("No authentication manager set. Password won't be re-checked.");
//        }

        //userRepository.findOneByName(currentUser.getName());
        //Assert.state(user != null, "Current user doesn't exist in database.");

        // userRepository.findOneByIdUpdatePassword(currentUser.getId(), passwordEncoder.encode(newPassword))
        //        .orElseThrow(() -> new OperationFailedException("change password"));
//    }

//    @Override
//    public UserDetails updatePassword(UserDetails user, String newPassword) {
//        // HSUserPrincipal currentUser = (HSUserPrincipal) this.securityContextHolderStrategy.getContext().getAuthentication().getPrincipal();
//        HSUserPrincipal currentUser = (HSUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        HSUser updatedUser = userRepository.findOneByIdUpdatePassword(currentUser.getId(), passwordEncoder.encode(newPassword))
//                .orElseThrow(() -> new OperationFailedException("change password"));
//
//        return new HSUserPrincipal(updatedUser);
//
//        return currentUser;
//    }

//    @Override
//    public boolean userExists(String username) {
//        return userRepository.doesNameExist(username);
//    }
}

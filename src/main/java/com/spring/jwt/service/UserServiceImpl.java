package com.spring.jwt.service;

import com.spring.jwt.Interfaces.UserService;
import com.spring.jwt.dto.PasswordChange;
import com.spring.jwt.dto.RegisterDto;
import com.spring.jwt.dto.ResponseDto;
import com.spring.jwt.dto.UserProfileDto;
import com.spring.jwt.entity.*;
import com.spring.jwt.exception.*;
import com.spring.jwt.repository.RoleRepository;
import com.spring.jwt.repository.UserProfileRepository;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.utils.BaseResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public BaseResponseDTO registerAccount(RegisterDto registerDto) {

        BaseResponseDTO response = new BaseResponseDTO();

        validateAccount(registerDto);

//        if (registerDto.getRoles().equalsIgnoreCase("ADMIN")) {
//            long adminCount = userRepository.countByRoleName("ADMIN");
//            if (adminCount >= 3) {
//               throw new UnauthorizedException ("You are not Authorized to Create This Account");
//            }
//        }
//        if (isDealerOrInspector(registerDto.getRoles()) && !userHasAdminAuthority()) {
//            throw new UnauthorizedException("User does not have the authority of ADMIN to create this account.");
//        }

        User user = insertUser(registerDto);

        try {
            userRepository.save(user);
            response.setCode(String.valueOf(HttpStatus.OK.value()));
            response.setMessage("Account Created !!!!");
        } catch (UserAlreadyExistException e) {
            response.setCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
            response.setMessage("User already exist !!");
        }catch (BaseException e){
            response.setCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
            response.setMessage("Invalid role !!");
        }
        return response;
    }

//    private boolean isDealerOrInspector(String role) {
//        return role.equals("DEALER") || role.equals("INSPECTOR");
//    }
//
//    private boolean userHasAdminAuthority() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return authentication != null && authentication.getAuthorities().stream()
//                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
//    }

    private User insertUser(RegisterDto registerDto) {
        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setMobileNo(registerDto.getMobileNo());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByName(registerDto.getRoles());
        roles.add(role);
        user.setRoles(roles);

        if (role.getName().equals("USER")) {
            Userprofile profile = new Userprofile();
            profile.setAddress(registerDto.getAddress());
            profile.setCity(registerDto.getCity());
            profile.setFirstName(registerDto.getFirstName());
            profile.setLastName(registerDto.getLastName());

            user.setProfile(profile);
            profile.setUser(user);
        } else if (role.getName().equals("DEALER")) {

            Dealer dealer = new Dealer();
            dealer.setAddress(registerDto.getAddress());
            dealer.setArea(registerDto.getArea());
            dealer.setCity(registerDto.getCity());
            dealer.setStatus(registerDto.status);
            dealer.setFirstname(registerDto.getFirstName());
            dealer.setLastName(registerDto.getLastName());
            dealer.setMobileNo(registerDto.getMobileNo());
            dealer.setShopName(registerDto.getShopName());
            dealer.setEmail(registerDto.getEmail());

            user.setDealer(dealer);
            dealer.setUser(user);

        } else if (role.getName().equals("INSPECTOR")) {
            InspectorProfile inspectorProfile = new InspectorProfile();
            inspectorProfile.setAddress(registerDto.getAddress());
            inspectorProfile.setCity(registerDto.getCity());
            inspectorProfile.setFirstName(registerDto.getFirstName());
            inspectorProfile.setLastName(registerDto.getLastName());
            user.setInspectorProfile(inspectorProfile);
            inspectorProfile.setUser(user);

        } else if (role.getName().equals("SALESPERSON")) {
            SalesPerson salesPerson = new SalesPerson();

            salesPerson.setFirstName(registerDto.getFirstName());
            salesPerson.setLastName(registerDto.getLastName());
            salesPerson.setAddress(registerDto.getAddress());
            salesPerson.setArea(registerDto.getArea());
            salesPerson.setCity(registerDto.getCity());
            salesPerson.setJoiningdate(registerDto.getJoiningdate());
            salesPerson.setDocumentId(registerDto.getDocumentId());
            salesPerson.setStatus(registerDto.status);
            user.setSalesPerson(salesPerson);
             salesPerson.setUser(user);
        }
        return user;
    }

    private void validateAccount(RegisterDto registerDto) {
        if (ObjectUtils.isEmpty(registerDto)) {
            throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Data must not be empty");
        }

        User user = userRepository.findByEmail(registerDto.getEmail());
        if (!ObjectUtils.isEmpty(user)) {
            throw new UserAlreadyExistException(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Username already exists !!");
        }

        List<String> roles = roleRepository.findAll().stream().map(Role::getName).toList();
        if (!roles.contains(registerDto.getRoles())) {
            throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Invalid role");
        }
    }

    @Override
    public BaseResponseDTO changePassword(int id, PasswordChange passwordChange) {
        BaseResponseDTO response = new BaseResponseDTO();

        Optional<Userprofile> userOptional = userProfileRepository.findById(id);

        if (userOptional.isPresent()) {
            User user= userOptional.get().getUser();

            if (passwordEncoder.matches(passwordChange.getOldPassword(), user.getPassword())) {

                if (passwordChange.getNewPassword().equals(passwordChange.getConfirmPassword())) {

                    user.setPassword(passwordEncoder.encode(passwordChange.getNewPassword()));
                    userRepository.save(user);

                    response.setCode(String.valueOf(HttpStatus.OK.value()));
                    response.setMessage("Password changed");
                } else {
                    response.setCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
                    throw new InvalidPasswordException("New password and confirm password does not match");
                }
            } else {
                response.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
                throw new InvalidPasswordException("Invalid Password");
            }

        } else {

            throw new UserNotFoundExceptions("No user found");
        }
        return response;
    }

    @Override
    public List<UserProfileDto> getAllUsers(int pageNo) {

        //User user= new User();
        List<Userprofile> listOfUsers = userProfileRepository.findAll();

        if ((pageNo * 10) > listOfUsers.size() - 1) {
            throw new PageNotFoundException("Page not found");
        }


        if (listOfUsers.size() <= 0) {
            throw new UserNotFoundExceptions("User not found", HttpStatus.NOT_FOUND);
        }

        // System.out.println("List of users: " + listOfUsers.size());

        List<UserProfileDto> listOfUserDto = new ArrayList<>();

        int pageStart = pageNo * 25;
        int pageEnd = pageStart + 25;

        int diff = (listOfUsers.size()) - pageStart;

        for (int counter = pageStart, i = 1; counter < pageEnd; counter++, i++) {
            if (pageStart > listOfUsers.size()) {
                break;
            }
            Optional<User> users=userRepository.findById(listOfUsers.get(counter).getUser().getId());
            if(users.isEmpty()){throw new UserNotFoundExceptions("User not found ");}
            // System.out.println("*");

            UserProfileDto userProfileDto = new UserProfileDto(listOfUsers.get(counter),users.get());

            listOfUserDto.add(userProfileDto);

            if (diff == i) {
                break;
            }
        }
        return listOfUserDto;
    }

    private UserProfileDto convertToDto(Userprofile userprofile,User user ) {
        //User user = new User();
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setId(userprofile.getId());
        userProfileDto.setAddress(userprofile.getAddress());
        userProfileDto.setCity(userprofile.getCity());
        userProfileDto.setFirstName(userprofile.getFirstName());
        userProfileDto.setLastName(userprofile.getLastName());
        userProfileDto.setMobile_no(user.getMobileNo());
        userProfileDto.setEmail(user.getEmail());
        return userProfileDto;
    }

    @Override
    public UserProfileDto getUser(int id) {
        Optional<Userprofile> userOptional = userProfileRepository.findById(id);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundExceptions("User not found");
        }

        User user = userOptional.get().getUser();

        return convertToDto(userOptional.get(), user);
    }

    @Override
    public BaseResponseDTO editUser(UserProfileDto userProfileDto, int id) {

        BaseResponseDTO response = new BaseResponseDTO();

        Optional<Userprofile> user = userProfileRepository.findById(id);

        if(user.isPresent()){
            user.get().setFirstName(userProfileDto.getFirstName());
            user.get().setLastName(userProfileDto.getLastName());
            user.get().setAddress(userProfileDto.getAddress());
            user.get().getUser().setMobileNo(userProfileDto.getMobile_no());
            user.get().getUser().setEmail(userProfileDto.getEmail());
            user.get().setCity(userProfileDto.getCity());

            userProfileRepository.save(user.get());

            response.setCode(String.valueOf(HttpStatus.OK.value()));
            response.setMessage("User edited");
        } else {
            response.setCode(String.valueOf(HttpStatus.NOT_FOUND.value()));
            throw new UserNotFoundExceptions("No user found");
        }

        return response;
    }

    @Override
    @Transactional
    public BaseResponseDTO removeUser(int id) {
        BaseResponseDTO response = new BaseResponseDTO();

        Optional<Userprofile> user = userProfileRepository.findById(id);

        if(user.isPresent()){

            User users= user.get().getUser();

            userRepository.DeleteById(users.getId());

            response.setCode(String.valueOf(HttpStatus.OK.value()));
            response.setMessage("User deleted");

        } else {
            response.setCode(String.valueOf(HttpStatus.NOT_FOUND.value()));
            throw new UserNotFoundExceptions("No user found");
        }

        return response;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public ResponseDto forgotPass(String email, String resetPasswordLink, String domain) throws UserNotFoundExceptions {
        ResponseDto response = new ResponseDto();

        User user = userRepository.findByEmail(email);

        if (user != null) {

            String message = "Hello this is Aniket";

            String resetLink = resetPasswordLink;

            String subject = "Checking: confirmation";

            String from = "b.aniket1414@gmail.com";

            String to = email;

            sendEmail(message, subject, to, from, resetLink, domain);

            response.setStatus(String.valueOf(HttpStatus.OK.value()));
            response.setMessage("Email sent");

        } else {

            response.setStatus(String.valueOf(HttpStatus.NOT_FOUND.value()));
            response.setMessage("User not found");
            throw new UserNotFoundExceptions("User not found");
        }

        return response;
    }

    // @Override
    public void sendEmail(String message, String subject, String to, String from, String resetLink, String domain) {

        String host = "smtp.gmail.com";

        Properties properties = System.getProperties();

        System.out.println(properties);

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");


        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("b.aniket1414@gmail.com", "egmqlowlfodymfzw");
            }

        });

        String content = "To reset your password, click here: " + resetLink.replace("169.254.63.118:5173", domain);

        MimeMessage m = new MimeMessage(session);

        try {
            m.setFrom(from);

            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            m.setSubject(subject);

            m.setText(content);
            Transport.send(m);

        } catch (MessagingException e) {
            e.printStackTrace();

        }
    }

    public void updateResetPassword(String token, String email) throws UserNotFoundExceptions {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            user.setResetPasswordToken(token);
            userRepository.save(user);
        } else {
            throw new UserNotFoundExceptions("could not find any user with this email");
        }
    }

    public ResponseDto updatePassword(String token, String newPassword) {
        ResponseDto response = new ResponseDto();

        User user = userRepository.findByResetPasswordToken(token);

        if (user != null) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

            String encodedPassword = bCryptPasswordEncoder.encode(newPassword);

            user.setPassword(encodedPassword);

            user.setResetPasswordToken(null);

            userRepository.save(user);

            response.setStatus(String.valueOf(HttpStatus.OK.value()));
            response.setMessage("Successful");
        } else {
            response.setStatus(String.valueOf(HttpStatus.OK.value()));
            response.setMessage("Email sent");

            throw new UserNotFoundExceptions("Something went wrong");
        }

        return response;
    }

}

package com.e_commerce;

import com.e_commerce.dao.AdminDao;
import com.e_commerce.dao.RoleDao;
import com.e_commerce.dao.UserDao;
import com.e_commerce.entity.Admin;
import com.e_commerce.entity.Role;
import com.e_commerce.services.UserService;
import com.e_commerce.services.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication

@RequiredArgsConstructor
@Configuration @EnableAutoConfiguration  @ComponentScan
@ComponentScan(basePackages = {"com.e-commerce"})
public class ECommerceApplication implements CommandLineRunner {

	@Autowired
	public UserService userService;

	private final UserDao userDao;
	private final RoleDao roleDao;
	private final AdminDao adminDao;
	private final PasswordEncoder passwordEncoder;



	public static void main(String[] args) {
		SpringApplication.run(ECommerceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Role role= Role.builder()
				.roleName("SUPER_ADMIN")
				.roleDescription("SUPER_ADMIN")
				.build();
		System.out.println("asdasdasdasd:"+passwordEncoder.encode("superadmin"));

		if( !roleDao.existsById("SUPER_ADMIN") && !adminDao.existsByRoles(role) ){

			roleDao.save(role);

			Admin admin= Admin.builder()
					.name("super admin")
					.email("superadmin@gmail.com")
					.serviceNo("12345")
					.contact("1234567890")
					.isActive(true)
					.roles(Set.of(role))
					.password(passwordEncoder.encode("superadmin"))
					.build();
			adminDao.save(admin);
		}
//		if(  ){
//
//		}




//		public void run(String... args) throws Exception {
//			if (!userService.isSuperAdminExists()) {
//				// Create a new User object and assign it as a super admin
//				User superAdmin = new User();
//				superAdmin.setUsername("admin");
//				superAdmin.setPassword("admin123");
//				superAdmin.setEmail("admin@example.com");
//				superAdmin.setRole("ROLE_ADMIN");
//
//				userService.saveUser(superAdmin);
//				System.out.println("Super admin created successfully.");
//			} else {
//				System.out.println("Super admin already exists. No more super admins can be created.");
//				// Alternatively, you can throw an exception here if you want to stop the application startup.
//				// throw new IllegalStateException("Super admin already exists.");
//			}
//		userService.initRoleAndUser();

	}
}

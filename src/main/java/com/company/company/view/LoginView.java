package com.company.company.view;


import java.io.IOException;

import com.company.company.client.RestClient;
import com.company.company.model.entity.Employee;
import com.company.company.model.entity.Token;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;

@Route("")
public class LoginView extends VerticalLayout {
	
	private TextField email = new TextField("Email");
	private PasswordField password = new PasswordField("Password");
	
	private Binder<Employee> binder = new Binder<>(Employee.class);
	
	Button login = new Button("Login");
	
	Button register = new Button("Register");
	
	private RestClient rc = new RestClient();
	
	public LoginView()  {
		
		login.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		
		login.addClickListener(e -> confirmLogin());
		
		register.addClickListener(event -> register.getUI().ifPresent(ui -> ui.navigate("register")));
		
		binder.forField(email)
	    .asRequired("Please fill in your email")
	    .bind(Employee::getEmail, Employee::setEmail);
	    
	    binder.forField(password)
	    .asRequired("Please fill in your password")
	    .bind(Employee::getPassword, Employee::setPassword);
	    
	    binder.bindInstanceFields(this);
		
		add(email,password,login, register);
	}
	
	private void confirmLogin() {
		if(binder.isValid()) {
			Token token = checkCredentials(email.getValue(), password.getValue());
			if(token != null) {
				if(token.getEmployee().getRoles().get(0).getRid() == 1) {
					UI.getCurrent().getSession().setAttribute("logManager", token);
					login.getUI().ifPresent(ui ->{ ui.navigate("manager"); });
				}
				else {
					UI.getCurrent().getSession().setAttribute("logEmployee", token);
					login.getUI().ifPresent(ui -> ui.navigate("employee"));
				}
			}
		}
		else {
			Notification n = new Notification("Please fill in your email and password");
			n.setDuration(3000);
			n.setPosition(Position.TOP_START);
			n.open();
		}
	}
	
	private Token checkCredentials(String emailUser, String passwordUser) {
		
		
		try {

			String data = rc.getData("http://localhost:8080/login/"+emailUser+"/"+passwordUser);
			
			ObjectMapper mapper = new ObjectMapper();
			
			Token token = new Token();
			
			token = mapper.readValue(data, Token.class);
			
			if(token != null) {
				
				return token;
			}
			else {
				
				Notification n = new Notification("Wrong credentials");
				n.setDuration(3000);
				n.setPosition(Position.TOP_START);
				n.open();
				
				return null;
			}
	
		} catch (IOException e) {
			Notification n = new Notification("Wrong credentials");
			n.setDuration(3000);
			n.setPosition(Position.TOP_START);
			n.open();
			return null;
		}
		
	}
	
}

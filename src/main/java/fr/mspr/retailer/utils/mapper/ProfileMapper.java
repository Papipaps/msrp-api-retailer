package fr.mspr.retailer.utils.mapper;

import fr.mspr.retailer.data.dto.Adress;
import fr.mspr.retailer.data.dto.Company;
import fr.mspr.retailer.data.dto.CustomerDTO;
import fr.mspr.retailer.data.model.Order;
import fr.mspr.retailer.data.model.Profile;

import java.util.ArrayList;
import java.util.Collection;

public class ProfileMapper {

     public static CustomerDTO toCustomerDTO(Profile profile) {
         Collection<Order> orders = profile.getOrders();
         return CustomerDTO.builder()
                     .id(profile.getId())
                     .name(profile.getFirstName() + " " + profile.getLastName())
                     .username(profile.getUsername())
                     .firstName(profile.getFirstName())
                     .lastName(profile.getLastName())
                     .address(Adress.builder()
                             .city(profile.getCity())
                             .postalCode(profile.getPostalCode())
                             .build())
                     .company(Company.builder()
                             .companyName(profile.getCompanyName())
                             .build())
                     .orders(new ArrayList<>(orders))
                     .createdAt(profile.getCreatedAt())
                     .build();

     }


 }

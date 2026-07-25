package com.fooddelivery.dto.address;

public record CustomerAddressResponse(Long id, Long customerId, String label, String addressLine1, String addressLine2,
                                      String city, String state, String postalCode) { }

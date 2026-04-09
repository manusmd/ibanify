package com.manuweb.ibanifycore.service.iban;

import com.manuweb.ibanifycore.entities.OpenIbanApiResponse;

public interface IbanValidationPort {

  OpenIbanApiResponse validate(String normalizedIban);
}

package com.wallet.service.interfaces;

import com.wallet.dto.RequestAdditionDTO;
import com.wallet.dto.RequestDTO;
import com.wallet.dto.RequestSubtractionDTO;

public interface IRequestService {

    RequestDTO createRequestSubtraction(RequestSubtractionDTO subtraction, String token);

    RequestDTO createRequestAddition(RequestAdditionDTO addition, String token);

}

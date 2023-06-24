package com.wallet.service.interfaces;

import com.wallet.dto.RequestDTO;
import com.wallet.dto.RequestSubtractionDTO;

public interface IRequestService {

    RequestDTO createRequestSubtraction (RequestSubtractionDTO subtraction, String token, String tokenMobile);

}

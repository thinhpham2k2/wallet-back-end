package com.wallet.service.interfaces;

import com.wallet.dto.*;

import java.util.List;

public interface IRequestService {

    RequestDTO createRequestSubtraction(RequestSubtractionDTO subtraction, String token);

    RequestDTO createRequestAddition(RequestAdditionDTO addition, String token);

    RequestDTO createRequest(RequestCreationDTO creation, String token);

    List<RequestExtraDTO> getRequestsByWalletList(String token, String customerId);
}

package com.wallet.service;

import com.wallet.dto.*;
import com.wallet.entity.*;
import com.wallet.jwt.JwtTokenProvider;
import com.wallet.mapper.RequestExtraMapper;
import com.wallet.mapper.RequestMapper;
import com.wallet.repository.*;
import com.wallet.service.interfaces.IFirebaseMessagingService;
import com.wallet.service.interfaces.IRequestService;
import com.wallet.webhooks.DiscordWebhook;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestService implements IRequestService {

    private final TypeRepository typeRepository;

    private final WalletRepository walletRepository;

    private final RequestRepository requestRepository;

    private final PartnerRepository partnerRepository;

    private final ProgramRepository programRepository;

    private final MembershipRepository membershipRepository;

    private final IFirebaseMessagingService firebaseService;

    private final TransactionRepository transactionRepository;

    private final RequestTypeRepository requestTypeRepository;

    @Override
    public RequestDTO createRequest(RequestCreationDTO creation, String token) {
        String userName;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Invalid JWT token");
        }
        if (programRepository.existsProgramByStatusAndStateAndToken(true, true, token)) {
            //Get partner
            Optional<Partner> partner = partnerRepository.findPartnerByUserNameAndStatus(userName, true);
            //Check partner and partner's program
            if (partner.isPresent()) {
                //Get membership
                Optional<Membership> membership = membershipRepository.findByCustomerIdAndStatus(true, creation.getCustomerId(), token);
                //Check membership
                if (membership.isPresent()) {
                    //Get request type
                    Optional<RequestType> requestType = requestTypeRepository.findRequestTypeByStatusAndId(true, 2L);
                    //Check request type
                    if (requestType.isPresent()) {
                        //Create new request
                        Request newRequest = requestRepository.save(new Request(null, creation.getAmount(), LocalDate.now(), LocalDate.now(), creation.getDescription(), true, true, partner.get(), requestType.get(), null));
                        //Get transaction type
                        Optional<Type> type = typeRepository.findTypeByStatusAndId(true, 2L);
                        //Check request type
                        if (type.isPresent()) {
                            //Get wallet
                            Optional<Wallet> wallet = walletRepository.findFirstByStatusAndTypeIdAndMembershipId(true, 1L, membership.get().getId());
                            //Check wallet
                            if (wallet.isPresent()) {
                                //Update membership's total expenditure
                                membership.get().setTotalExpenditure(membership.get().getTotalExpenditure().add(creation.getAmount()));
                                transactionRepository.save(new Transaction(null, creation.getAmount(), LocalDate.now(), LocalDate.now(), creation.getDescription(), true, true, type.get(), wallet.get(), newRequest));
                                //Get the list of next levels
                                List<Level> nextLevels = programRepository.getProgramByStatusAndToken(true, token).getProgramLevelList().stream().map(ProgramLevel::getLevel).filter(l -> l.getCondition().compareTo(membership.get().getLevel().getCondition()) > 0).toList();
                                //Get the next levels
                                Optional<Level> level = nextLevels.stream().filter(l -> l.getCondition().compareTo(membership.get().getTotalExpenditure()) <= 0).max(Comparator.comparing(Level::getCondition));
                                if (level.isPresent()) {
                                    membership.get().setLevel(level.get());
                                    try {
                                        NoteDTO note = new NoteDTO();
                                        note.setImage("");
                                        note.setData(new HashMap<>());
                                        note.setSubject("Level up !");
                                        note.setContent("You have reached the " + level.get().getLevel() + " level");
                                        //Push notification about level up
                                        firebaseService.sendNotification(note, creation.getToken());
                                    } catch (Exception e) {
                                        System.out.println("Not found mobile token to push notification");
                                    }
                                }
                                //Update membership's level and total expenditure

                                membershipRepository.save(membership.get());
                                try {
                                    NoteDTO note = new NoteDTO();
                                    note.setImage("");
                                    note.setData(new HashMap<>());
                                    note.setSubject("Payment success !");
                                    note.setContent("You have completed the payment successfully");
                                    //Push notification about level up
                                    firebaseService.sendNotification(note, creation.getToken());
                                } catch (Exception e) {
                                    System.out.println("Not found mobile token to push notification");
                                }

                                try {
                                    DiscordWebhook webhook = new DiscordWebhook();
                                    DiscordWebhook.EmbedObject embedObjects = new DiscordWebhook.EmbedObject();
                                    embedObjects.setAuthor(partner.get().getFullName(), null, partner.get().getImage());
                                    embedObjects.addField("Program", membership.get().getProgram().getProgramName(), false);
                                    embedObjects.addField("Customer", membership.get().getCustomer().getFullName(), false);
                                    embedObjects.addField("Transaction", "- " + creation.getAmount() + " point", false);
                                    embedObjects.setFooter(creation.getDescription(), null);
                                    webhook.addEmbed(embedObjects);
                                    webhook.execute();
                                } catch (IOException e) {
                                    System.out.println("Webhooks fails");
                                }

                                return RequestMapper.INSTANCE.toDTO(newRequest);
                            } else {
                                throw new InvalidParameterException("Not found wallet");
                            }
                        } else {
                            throw new InvalidParameterException("Not found transaction type");
                        }
                    } else {
                        throw new InvalidParameterException("Not found request type");
                    }
                } else {
                    throw new InvalidParameterException("Invalid customer information");
                }
            } else {
                throw new InvalidParameterException("Invalid partner information");
            }
        } else {
            throw new InvalidParameterException("Invalid program token");
        }
    }

    @Override
    public RequestDTO createRequestAddition(RequestAdditionDTO addition, String token) {
        String userName;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Invalid JWT token");
        }
        if (programRepository.existsProgramByStatusAndStateAndToken(true, true, token)) {
            //Get partner
            Optional<Partner> partner = partnerRepository.findPartnerByUserNameAndStatus(userName, true);
            //Check partner and partner's program
            if (partner.isPresent()) {
                //Get membership
                Optional<Membership> membership = membershipRepository.findByCustomerIdAndStatus(true, addition.getCustomerId(), token);
                //Check membership
                if (membership.isPresent()) {
                    //Get request type
                    Optional<RequestType> requestType = requestTypeRepository.findRequestTypeByStatusAndId(true, 1L);
                    //Check request type
                    if (requestType.isPresent()) {
                        //Create new request
                        Request newRequest = requestRepository.save(new Request(null, addition.getAmount(), LocalDate.now(), LocalDate.now(), addition.getDescription(), true, true, partner.get(), requestType.get(), null));
                        //Get transaction type
                        Optional<Type> type = typeRepository.findTypeByStatusAndId(true, 1L);
                        //Check request type
                        if (type.isPresent()) {
                            //Get wallet
                            Optional<Wallet> wallet = walletRepository.findWalletByStatusAndId(true, addition.getWalletId());
                            //Check wallet
                            if (wallet.isPresent()) {
                                //Update wallet date updated
                                wallet.get().setDateUpdated(LocalDate.now());
                                //Update membership's total receipt
                                membership.get().setTotalReceipt(membership.get().getTotalReceipt().add(addition.getAmount()));
                                //Update wallet's total receipt
                                wallet.get().setTotalReceipt(wallet.get().getTotalReceipt().add(addition.getAmount()));
                                transactionRepository.save(new Transaction(null, addition.getAmount(), LocalDate.now(), LocalDate.now(), addition.getDescription(), true, true, type.get(), wallet.get(), newRequest));
                                //Update wallet's balance
                                wallet.get().setBalance(wallet.get().getBalance().add(addition.getAmount()));
                                walletRepository.save(wallet.get());
                                //Update membership's level and total expenditure
                                membershipRepository.save(membership.get());

                                try {
                                    NoteDTO note = new NoteDTO();
                                    note.setImage("");
                                    note.setData(new HashMap<>());
                                    note.setSubject("Recharge success !");
                                    note.setContent("You have successfully deposited " + addition.getAmount() + " units of coins into the " + wallet.get().getType().getType());
                                    //Push notification about level up
                                    firebaseService.sendNotification(note, addition.getToken());
                                } catch (Exception e) {
                                    System.out.println("Not found mobile token to push notification");
                                }

                                try {
                                    DiscordWebhook webhook = new DiscordWebhook();
                                    DiscordWebhook.EmbedObject embedObjects = new DiscordWebhook.EmbedObject();
                                    embedObjects.setAuthor(partner.get().getFullName(), null, partner.get().getImage());
                                    embedObjects.addField("Program", membership.get().getProgram().getProgramName(), false);
                                    embedObjects.addField("Customer", membership.get().getCustomer().getFullName(), false);
                                    embedObjects.addField("Transaction", "+ " + addition.getAmount() + " point", false);
                                    embedObjects.setFooter(addition.getDescription(), null);
                                    webhook.addEmbed(embedObjects);
                                    webhook.execute();
                                } catch (IOException e) {
                                    System.out.println("Webhooks fails");
                                }

                                return RequestMapper.INSTANCE.toDTO(newRequest);
                            } else {
                                throw new InvalidParameterException("Not found wallet");
                            }
                        } else {
                            throw new InvalidParameterException("Not found transaction type");
                        }
                    } else {
                        throw new InvalidParameterException("Not found request type");
                    }
                } else {
                    throw new InvalidParameterException("Invalid customer information");
                }
            } else {
                throw new InvalidParameterException("Invalid partner information");
            }
        } else {
            throw new InvalidParameterException("Invalid program token");
        }
    }

    @Override
    public RequestDTO createRequestSubtraction(RequestSubtractionDTO subtraction, String token) {
        String userName;
        try {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
            userName = jwtTokenProvider.getUserNameFromJWT(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidParameterException("Invalid JWT token");
        }
        if (programRepository.existsProgramByStatusAndStateAndToken(true, true, token)) {
            //Get partner
            Optional<Partner> partner = partnerRepository.findPartnerByUserNameAndStatus(userName, true);
            //Check partner and partner's program
            if (partner.isPresent()) {
                //Get membership
                Optional<Membership> membership = membershipRepository.findByCustomerIdAndStatus(true, subtraction.getCustomerId(), token);
                //Check membership
                if (membership.isPresent()) {
                    //Get membership's wallet list
                    List<Wallet> walletList = membership.get().getWalletList().stream().filter(w -> w.getStatus().equals(true)).toList();
                    //Get membership's wallet ID list
                    Set<Long> walletsOwner = walletList.stream().map(Wallet::getId).collect(Collectors.toSet());
                    //Check request's wallet ID list
                    if (walletsOwner.containsAll(subtraction.getWalletIds())) {
                        Set<Long> commonIds = new HashSet<>(walletsOwner);
                        //Get membership and request wallet ID list
                        commonIds.retainAll(subtraction.getWalletIds());
                        //Get membership and request wallet list
                        List<Wallet> wallets = walletList.stream().filter(w -> commonIds.contains(w.getId())).toList();
                        //Get total balance request wallet list
                        BigDecimal totalBalance = wallets.stream().map(Wallet::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
                        if (totalBalance.compareTo(subtraction.getAmount()) >= 0) {
                            //Get request type
                            Optional<RequestType> requestType = requestTypeRepository.findRequestTypeByStatusAndId(true, 2L);
                            //Check request type
                            if (requestType.isPresent()) {
                                //Create new request
                                Request newRequest = requestRepository.save(new Request(null, subtraction.getAmount(), LocalDate.now(), LocalDate.now(), subtraction.getDescription(), true, true, partner.get(), requestType.get(), null));
                                //Get transaction type
                                Optional<Type> type = typeRepository.findTypeByStatusAndId(true, 2L);
                                //Check request type
                                if (type.isPresent()) {
                                    BigDecimal amount = subtraction.getAmount();
                                    //Create wallet list loop
                                    for (Wallet wallet : wallets) {
                                        //Update wallet date updated
                                        wallet.setDateUpdated(LocalDate.now());
                                        //Check if the balance in your wallet is less than total spending
                                        if (wallet.getBalance().compareTo(amount) < 0) {
                                            amount = amount.subtract(wallet.getBalance());
                                            //Update membership's total expenditure
                                            membership.get().setTotalExpenditure(membership.get().getTotalExpenditure().add(wallet.getBalance()));
                                            //Update wallet's total expenditure
                                            wallet.setTotalExpenditure(wallet.getTotalExpenditure().add(wallet.getBalance()));
                                            transactionRepository.save(new Transaction(null, wallet.getBalance(), LocalDate.now(), LocalDate.now(), subtraction.getDescription(), true, true, type.get(), wallet, newRequest));
                                            //Update wallet's balance
                                            wallet.setBalance(BigDecimal.ZERO);
                                            walletRepository.save(wallet);
                                        }
                                        //Check if the balance in your wallet is more than total spending
                                        else {
                                            //Update membership's total expenditure
                                            membership.get().setTotalExpenditure(membership.get().getTotalExpenditure().add(amount));
                                            //Update wallet's total expenditure
                                            wallet.setTotalExpenditure(wallet.getTotalExpenditure().add(amount));
                                            //Update wallet's balance
                                            wallet.setBalance(wallet.getBalance().subtract(amount));
                                            walletRepository.save(wallet);
                                            transactionRepository.save(new Transaction(null, amount, LocalDate.now(), LocalDate.now(), subtraction.getDescription(), true, true, type.get(), wallet, newRequest));
                                            break;
                                        }
                                    }
                                    //Get the list of next levels
                                    List<Level> nextLevelList = programRepository.getProgramByStatusAndToken(true, token).getProgramLevelList().stream().map(ProgramLevel::getLevel).filter(l -> l.getCondition().compareTo(membership.get().getLevel().getCondition()) > 0).toList();
                                    //Get the next levels
                                    Optional<Level> level = nextLevelList.stream().filter(l -> l.getCondition().compareTo(membership.get().getTotalExpenditure()) <= 0).max(Comparator.comparing(Level::getCondition));
                                    if (level.isPresent()) {
                                        membership.get().setLevel(level.get());
                                        try {
                                            NoteDTO note = new NoteDTO();
                                            note.setImage("");
                                            note.setData(new HashMap<>());
                                            note.setSubject("Level up !");
                                            note.setContent("You have reached the " + level.get().getLevel() + " level");
                                            //Push notification about level up
                                            firebaseService.sendNotification(note, subtraction.getToken());
                                        } catch (Exception e) {
                                            System.out.println("Not found mobile token to push notification");
                                        }
                                    }
                                    //Update membership's level and total expenditure
                                    membershipRepository.save(membership.get());

                                    try {
                                        NoteDTO note = new NoteDTO();
                                        note.setImage("");
                                        note.setData(new HashMap<>());
                                        note.setSubject("Payment success !");
                                        note.setContent("You have completed the payment successfully");
                                        //Push notification about transaction success
                                        firebaseService.sendNotification(note, subtraction.getToken());
                                    } catch (Exception e) {
                                        System.out.println("Not found mobile token to push notification");
                                    }

                                    try {
                                        DiscordWebhook webhook = new DiscordWebhook();
                                        DiscordWebhook.EmbedObject embedObjects = new DiscordWebhook.EmbedObject();
                                        embedObjects.setAuthor(partner.get().getFullName(), null, partner.get().getImage());
                                        embedObjects.addField("Program", membership.get().getProgram().getProgramName(), false);
                                        embedObjects.addField("Customer", membership.get().getCustomer().getFullName(), false);
                                        embedObjects.addField("Transaction", "- " + subtraction.getAmount() + " point", false);
                                        embedObjects.setFooter(subtraction.getDescription(), null);
                                        webhook.addEmbed(embedObjects);
                                        webhook.execute();
                                    } catch (IOException e) {
                                        System.out.println("Webhooks fails");
                                    }

                                    return RequestMapper.INSTANCE.toDTO(newRequest);
                                } else {
                                    throw new InvalidParameterException("Not found transaction type");
                                }
                            } else {
                                throw new InvalidParameterException("Not found request type");
                            }
                        } else {
                            throw new InvalidParameterException("Not enough balance to make a transaction");
                        }
                    } else {
                        throw new InvalidParameterException("Invalid wallet");
                    }
                } else {
                    throw new InvalidParameterException("Invalid customer information");
                }
            } else {
                throw new InvalidParameterException("Invalid partner information");
            }
        } else {
            throw new InvalidParameterException("Invalid program token");
        }
    }

    @Override
    public List<RequestExtraDTO> getRequestsByWalletList(String token, String customerId) {
        List<Wallet> walletList = walletRepository.findAllByProgramTokenAndCustomerId(true, customerId, token);
        if (!walletList.isEmpty()) {
            return transactionRepository.findAllRequestByWalletId(true, walletList.stream().map(Wallet::getId).collect(Collectors.toList())).stream().map(RequestExtraMapper.INSTANCE::toDTO).sorted(Comparator.comparingLong(RequestExtraDTO::getId).reversed()).toList();
        } else {
            throw new InvalidParameterException("Not found request list");
        }
    }
}

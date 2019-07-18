# htbhf-smart-stub
Service for stubbing out HMRC, DWP and Card Service provider apis.

## Benefit Eligibility Stubbing
The service returns stubbed responses depending on the national insurance number (nino) which is sent in the request.
The nino is encoded as follows:

* The first two characters determine the person's DWP eligibility status. (ELIGIBLE|INELIGIBLE|PENDING|NO_MATCH)
  

  'E' will return ELIGIBLE
  'I' will return INELIGIBLE
  'P' will return PENDING
  Any other character will return NO_MATCH
  
  The first character is checked for a match, if this results in a no match, the second character is then checked.

  e.g. 
       
       EB123456C will return ELIGIBLE according to DWP, 
       FE123456C will return ELIGIBLE according to DWP,
       PE123456C will return PENDING according to DWP,
       DB123456C will return NO_MATCH according to DWP.
       
    
* The first numeric digit represents the number of children under one. (Note, that if this is greater than the number of children under four, 
then the stub will return the same value for children under 1 and children under 4)
  e.g EA220000C will return a valid response with two children under one.
  
* The second numeric digit represents the number of children under four.
  e.g EA030000C will return a valid response with three children under four.
  
* The NINO ZZ999999D can be used if you want to trigger an error within the Smart stub, which will in turn return a 500 response.

* Note that the number of children returned will be dependant upon the same digits for both DWP and HMRC requests.

The household identifier value is a Base64 generated value based on the NINO, DWP and HMRC requests will return different household identifiers.

## postman collection
Use https://www.getpostman.com/collections/b1e8a55b936abd3879e3 to import a postman collection with api examples.

## Card Service Provider Stubbing

The service returns responses based upon the request sent to the following three api endpoints:

### Create Card

Card number will be returned from a request to create a card and this will trigger all the scenarios below.
The first name will be used to trigger a response from a request to create a new card, and the subsequent
calls will all be triggered from the card id provided from the first call. The first digit of the card id
will define which scenarios below are to be triggered. All first name matching will be case insensitive.
We have specifically left a few prefixes unused so that we have some spare should any further scenarios
arise that we want to cater for.

#### Scenario 1 - Default, Card prefix: 9
Card creation will be successful and a card id will be returned. If the first name doesn't match any other scenarios listed
below then the card prefix will be returned as 9.

#### Scenario 2 - First name: CardError
A first name of CardError will cause the card creation to fail.

### Current Balance

Scenarios for the current balance request are based off the cardId returned from the createCard request,
which is in turn based off the first name provided for that request. Both the available balance and ledger balance
(unreconciled balance) will be returned and will always be the same amount.

#### Scenario 1 - Default, Card prefix: 9

Balance returned is random between 0p and £12.39 (1p less than 4 week's worth of a single voucher,
hence will always allow a full top-up to be made.

#### Scenario 2 - First name: NoTopup, Card prefix: 1

A first name of NoTopup for the card creation request will return a cardId prefixed with 1, which will
in turn return a balance which is too high to allow a top-up (e.g. £1000 just to make sure)

#### Scenario 3 - First name: Partial, Card prefix: 2

A first name of Partial for the card creation request will return a cardId prefixed with 2, which will
in turn return a balance which is 6 times weekly entitlement for a pregnant woman with no children (£18.60).
This will allow a partial payment to be made (2 weeks). Note that this partial payment test is only guaranteed
to work for a pregnant woman with no children

#### Scenario 4 - First name: BalanceError, Card prefix: 3

A first name of BalanceError for the card creation request will return a cardId prefixed with 3, which will
in turn will cause the balance check to fail (create card will have succeeded)

### New payments

#### Scenario 1 - Default, Card prefix: 9

Any first name not listed above will result in a successful payment.

#### Scenario 2 - First name: PaymentError, Card prefix: 4

A first name of PaymentError for the card creation request will return a cardId prefixed with 4, which will
in turn cause the card payment to fail (create card and balance check will have succeeded)

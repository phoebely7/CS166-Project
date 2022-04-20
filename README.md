# CS166-Project

## Authors: Ryan Lam and Phoebe Ly

This client application allows users to manage an online Booking System for multiple Cruises. The system allows us to navigate and manipulate information on Cruises available and the Customer who wants to book. Information regarding the Ship, Captain, and Cruise can be added.

### Add Ship

This function requires Ship ID to not exist in the database before it is added. 'Make' and 'Model' is a string and can include characters such as '-' and '_'.  Integers must be non-negative.

### Add Captain

This function requires Captain ID to not exist in the database before it is added.

### Add Cruise

This function requires Cruise ID to not exist in the database before it is added. Integers must be non-negative and the inputted Date must be valid (explain what this means).

### Book Cruise

We made the assumption that "Confirmed" means the customer is definitely going to go on the cruise, and this function is used to confirm them if they are "Reserved". i.e If the customer's status is "Reserved", we changed it to "Confirmed".

Our conditions:
  Customer ID and Cruise ID must exist in the database
  
  When Reservation exists:
  
    * If C, status stays the same.
    * If R, change to C.
    * If W and if there are available seats, change to R. Otherwise it will stay as W.

  When Reservation does not exist:
  
    * If there are available seats, add Reservation with status 'R'
    * If there are no available seats, add Reservation with status 'W'
 
After a Reservation is added, number of tickets sold is incremented in Cruise.

### List Number of available seats for a Given Cruise

Available seats is defined as 'Total Ship Capacity - Number of Tickets Sold'. This is updated when a new Reservation is booked.

### List total number of repairs per ship in descending order

This function counts Total Repairs per ship from Ship.

### Find total number of passengers with given status

This function counts the number of Customers within each status: 'W', 'R', 'C'.

### Helper Functions

  * validateString() - Can include digits
  * validateInteger() / isInteger() - No characters allowed
  * checkIDExists() - Makes sure that ID number (either Cruise, Customer, or Reservation) exists in the database.
  * validateDate() / isValidDate() - Makes sure inputted Date is correct and formatted properly

### Extra functionality

* Delete(): We added a function that will allow you to delete records that you added to the database. This makes it convenient in case you accidentally add wrong information.
* Print(): This function prints all of the reservations given a customers name. We assume that everyone has a unique name.


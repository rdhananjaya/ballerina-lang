
import ballerina/time;
import ballerina/io;

function planTrip(string lastname, time:Time date) {
    string hotelRID;
    string flightRID;
    string lastName = "Middleton";
    
    transaction compensation with id = "t1" {
        hotelRID = reserveHotel(lastName, date);
        flightRID = reserveFlight(lastName, date);
    }

    if (hotelRID == "-1" || flightRID == "-1") {
        compensate "t1";
    }

    transaction compensation with id = "t2" {
        transFoo(hotelRID);
    }
}

function reserveHotel(string lastName, time:Time date) returns string {
    string hotelRID = "hotelResId";
    chargeCreditCard();
    writeDataToDB();
    return hotelRID;

    //var p = function () { return hotelRID; };
    
    oncompensate { 
        cancelHotel(lastName, date, hotelRID); 
        compensate;
    }
}

function writeDataToDB() {
    io:println("write db!");
}

function reserveFlight(string lastName, time:Time date) returns string {
    io:println("reserve Flight!");
    return "fakeFlightId";
}

function transFoo(string hotelId) {
    
}

function cancelHotel(string lName, time:Time date, string hotelRID) {
    undoDBChanges();
}

function undoDBChanges() {
    
}

function chargeCreditCard() {
    creditCardCharge();
    oncompensate {
        reverseCreditCardCharge();
    }
}

function creditCardCharge() {
    
}

function reverseCreditCardCharge() {

}

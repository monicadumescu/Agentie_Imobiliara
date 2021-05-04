package org.loose.fis.sre.services;
import org.loose.fis.sre.services.UserService;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectRepository;
import org.loose.fis.sre.model.Booking;
import org.loose.fis.sre.model.User;
import org.loose.fis.sre.model.House;
import org.loose.fis.sre.exceptions.BookingAlreadyExistsException;
import org.loose.fis.sre.exceptions.IncorectCredentials;
import org.loose.fis.sre.exceptions.HouseDoesNotExistsException;
import org.loose.fis.sre.exceptions.IncorrectDateException;
import org.loose.fis.sre.exceptions.AgentDoesNotExistException;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static org.loose.fis.sre.services.FileSystemService.getPathToBooking;

public class BookingService {

    private static ObjectRepository<Booking> bookingRepository;
    //private static ObjectRepository<House> houseRepository;

    public static void initDatabase() {
        Nitrite database = Nitrite.builder()
                .filePath(getPathToBooking("booking-database.db").toFile())
                .openOrCreate("agent_imb", "agent_imob");

        bookingRepository = database.getRepository(Booking.class);
    }
    public static ObjectRepository<Booking> getBookingRepository()
    {
        return bookingRepository;
    }
    public static void addBooking(String address,String day,String month,String year,String hour, String agent_book, String special_req,String user) throws BookingAlreadyExistsException, IncorectCredentials, HouseDoesNotExistsException, IncorrectDateException, AgentDoesNotExistException
    {
        checkBookingDoesNotAlreadyExist(day,month,year,hour,agent_book);
        UserService.checkAgentDoesExist(agent_book);
        //checkAddressDoesNotAlreadyExist(address);
        UserService.checkUsername(user);
        HouseService.checkAddressDoesExist(address);
        checkDate(day,month,year);
        bookingRepository.insert(new Booking(address,day,month,year,hour,agent_book,special_req,user));
    }

    private static void checkBookingDoesNotAlreadyExist(String day,String month,String year,String hour,String agent_book) throws BookingAlreadyExistsException {
        for (Booking booking : bookingRepository.find()) {
            if (Objects.equals(day, booking.getDay()) && Objects.equals(month, booking.getMonth()) && Objects.equals(year, booking.getYear()) && Objects.equals(hour, booking.getHour())  && Objects.equals(agent_book, booking.getAgent_book()))
                throw new BookingAlreadyExistsException(day,month,year,hour,agent_book);
        }
    }
    private static void checkDate(String day,String month,String year) throws IncorrectDateException {
        if((Objects.equals(day,"30") || Objects.equals(day,"31")) &&(Objects.equals(month,"February")))
        {
            throw new IncorrectDateException(day,month,year);
        }
        if((Objects.equals(day,"31")) && (Objects.equals(month,"April") || Objects.equals(month,"June") || Objects.equals(month,"September") ||  Objects.equals(month,"November")))
        {
            throw new IncorrectDateException(day,month,year);
        }
    }
    /*private static void checkAddressDoesNotAlreadyExist(String address) throws HouseDoesNotExistsException {
        for (House house : houseRepository.find()) {
            if (Objects.equals(address, house.getAddress()))
                return;
            throw new HouseDoesNotExistsException(address);
        }
    }*/

    public static String  seeBookings(String Name) throws IncorectCredentials
    {
        UserService.CheckNameCredentials(Name);
        String s="";
        for (Booking  booking : bookingRepository.find())
        {
            if(Objects.equals(Name, booking.getAgent_book())) {
                s = s + booking.toString();
                s = s + "\n";
            }
        }
        return s;
    }
}
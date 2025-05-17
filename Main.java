import java.util.*;
import java.util.regex.*;
import java.io.*;

public class Main {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Scanner input = new Scanner(System.in);
        while(input.hasNext()) {
            commandSwitch(input.nextLine());
        }
        input.close();

    }

    private static void add(String args) {
        LinkedList<Tour> tours = new Tours().getTours();
        String[] tourParams = args.split(";");
        if (tourParams.length != 6) {
            System.out.println("wrong field count");
            return;
        }
        if (!isIDValid(tourParams[0]) || isIDUsed(tours, tourParams[0])) {
            System.out.println("wrong id");
            return;
        }

        if (!isDateValid(tourParams[2])) {
            System.out.println("wrong date");
            return;
        }
        if (!isDayCountValid(tourParams[3])) {
            System.out.println("wrong day count");
            return;
        }
        if (!isPriceValid(tourParams[4])) {
            System.out.println("wrong price");
            return;
        }
        if (!isVehicleValid(tourParams[5])) {
            System.out.println("wrong vehicle");
            return;
        }

        tours.add(new Tour(
                Integer.parseInt(tourParams[0]),
                formatCityName(tourParams[1]),
                tourParams[2],
                Integer.parseInt(tourParams[3]),
                formatPrice(tourParams[4]),
                tourParams[5].toUpperCase()
        ));

        try {
            writeFile(tours);
            System.out.println("added");
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    private static void del(String args) {
        LinkedList<Tour> tours = new Tours().getTours();
        if (!isIDValid(args) || !isIDUsed(tours, args)) {
            System.out.println("wrong id");
            return;
        }
        Tour tourToRemove = null;
        for (Tour tour : tours) {
            if (tour.id == Integer.parseInt(args)) {
                tourToRemove = tour;
                break;
            }
        }
        if (tourToRemove != null) {
            tours.remove(tourToRemove);
            try {
                writeFile(tours);
                System.out.println("deleted");
            } catch (IOException err) {
                err.printStackTrace();
            }
        } else {
            System.out.println("Tour not found");
        }
    }

    private static void edit(String args) {
        LinkedList<Tour> tours = new Tours().getTours();
        String[] tourParams = args.split(";");
        if (tourParams.length != 6) {
            System.out.println("wrong field count");
            return;
        }
        if (!isIDValid(tourParams[0]) || !isIDUsed(tours, tourParams[0])) {
            System.out.println("wrong id");
            return;
        }

        if (tourParams[2].length() > 0 && !isDateValid(tourParams[2])) {
            System.out.println("wrong date");
            return;
        }
        if (tourParams[3].length() > 0 && !isDayCountValid(tourParams[3])) {
            System.out.println("wrong day count");
            return;
        }
        if (tourParams[4].length() > 0 && !isPriceValid(tourParams[4])) {
            System.out.println("wrong price");
            return;
        }
        if (tourParams[5].length() > 0 && !isVehicleValid(tourParams[5])) {
            System.out.println("wrong vehicle");
            return;
        }

        for (Tour tour : tours) if (tour.id == Integer.parseInt(tourParams[0])) {
            if (tourParams[1].length() > 0) {
                tour.city = formatCityName(tourParams[1]);
            }

            if (tourParams[2].length() > 0) {
                tour.date = tourParams[2];
            }

            if (tourParams[3].length() > 0) {
                tour.days = Integer.parseInt(tourParams[3]);
            }

            if (tourParams[4].length() > 0) {
                tour.price = formatPrice(tourParams[4]);
            }

            if (tourParams[5].length() > 0) {
                tour.vehicle = tourParams[5].toUpperCase();
            }
        }

        try {
            writeFile(tours);
            System.out.println("changed");
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    private static void print() {
        Tours tours = new Tours();

        printTableRowSplitter();
        printTableHeader();
        printTableRowSplitter();

        for (Tour tour : tours.getTours()) {
            tour.print();
            System.out.println();
        }

        printTableRowSplitter();
    }

    private static void sort() {
        LinkedList<Tour> tours = new Tours().getTours();
        for (int i = 0; i < tours.size(); i++) {
            for (int j = i + 1; j < tours.size(); j++) {
                String[] tour1DateParams = tours.get(i).date.split("/");
                Calendar tour1Date = Calendar.getInstance();
                tour1Date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tour1DateParams[0]));
                tour1Date.set(Calendar.MONTH, Integer.parseInt(tour1DateParams[1]));
                tour1Date.set(Calendar.YEAR, Integer.parseInt(tour1DateParams[2]));
                String[] tour2DateParams = tours.get(j).date.split("/");
                Calendar tour2Date = Calendar.getInstance();
                tour2Date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tour2DateParams[0]));
                tour2Date.set(Calendar.MONTH, Integer.parseInt(tour2DateParams[1]));
                tour2Date.set(Calendar.YEAR, Integer.parseInt(tour2DateParams[2]));
                Tour tmp = null;
                if (tour1Date.getTimeInMillis() > tour2Date.getTimeInMillis()) {
                    tmp = tours.get(i);
                    tours.set(i, tours.get(j));
                    tours.set(j, tmp);
                }
            }
        }
        try {
            writeFile(tours);
            System.out.println("sorted");
        } catch (IOException err) {
            err.printStackTrace();
        }

    }

    private static void find(String args) {
        Tours tours = new Tours();

        try {
            if (!args.matches("\\d+(\\.\\d+)?")) {
                throw new NumberFormatException();
            }
            double price = Double.parseDouble(args);
            printTableRowSplitter();
            printTableHeader();
            printTableRowSplitter();

            for (Tour tour : tours.getTours()) {
                if (tour.price <= price) {
                    tour.print();
                    System.out.println();
                }
            }
            printTableRowSplitter();

        } catch (NumberFormatException err) {
            System.out.println("wrong price");
        }
    }


    private static void avg() {
        Tours tours = new Tours();
        List<Tour> tourList = tours.getTours();
        double sum = 0;
        for (Tour tour : tourList) {
            sum += tour.price;
        }
        double average = sum / tourList.size();
        System.out.printf("average=%.2f%n", average);
    }


    private static void exit() {
        System.exit(0);
    }

    static void commandSwitch(String command) {
        String[] commands = command.trim().split("\\s+", 2);
        switch(commands[0]) {
            case "add":
                add(commands[1]);
                return;
            case "del":
                del(commands[1]);
                return;
            case "edit":
                edit(commands[1]);
                return;
            case "print":
                print();
                return;
            case "sort":
                sort();
                return;
            case "find":
                find(commands[1]);
                return;
            case "avg":
                avg();
                return;
            case "exit":
                exit();
                return;
            default:
                System.out.println("wrong command");
                return;
        }
    }

    private static void printTableRowSplitter() {
        for (int i = 0; i < 60; i++) System.out.print("-");
        System.out.println();
    }

    private static void printTableHeader() {
        System.out.printf("%-4s", "ID");
        System.out.printf("%-21s", "City");
        System.out.printf("%-11s", "Date");
        System.out.printf("%6s", "Days");
        System.out.printf("%10s", "Price");
        System.out.printf(" %-7s", "Vehicle");
        System.out.println();
    }

    private static String formatCityName(String city) {
        String[] cityName = city.split("\\s+");
        for (int i = 0; i < cityName.length; i++) {
            cityName[i] = cityName[i].substring(0, 1).toUpperCase() + cityName[i].substring(1);
        }
        return String.join(" ", cityName);
    }

    private static double formatPrice(String price) {
        price = price.replace(",", ".");
        return Double.parseDouble(String.format("%.2f", Double.parseDouble(price)));
    }

    private static void writeFile(LinkedList<Tour> tours) throws IOException {
        FileWriter fileWriter = new FileWriter("db.csv");
        for (Tour tour : tours) {
            fileWriter.write(tour.id + ";");
            fileWriter.write(tour.city + ";");
            fileWriter.write(tour.date + ";");
            fileWriter.write(tour.days + ";");
            fileWriter.write(tour.price + ";");
            fileWriter.write(tour.vehicle);
            fileWriter.write("\n");
        }
        fileWriter.close();
    }

    private static boolean isIDValid(String id) {
        if (!Pattern.matches("[1-9]{1}[0-9]{2}", id)) return false;
        return true;
    }

    private static boolean isIDUsed(LinkedList<Tour> tours, String id) {
        for (Tour tour : tours) if (tour.id == Integer.parseInt(id)) return true;
        return false;
    }

    private static boolean isDateValid(String date) {
        if (!Pattern.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}", date)) return false;
        String[] dateParams = date.split("/");
        if (Integer.parseInt(dateParams[0]) < 1 || Integer.parseInt(dateParams[0]) > 31) return false;
        if (Integer.parseInt(dateParams[1]) < 1 || Integer.parseInt(dateParams[1]) > 12) return false;
        return true;
    }

    private static boolean isDayCountValid(String dayCount) {
        try {
            if (Integer.parseInt(dayCount) < 0) {
                return false;
            }
        } catch (Exception err) {
            return false;
        }
        return true;
    }

    private static boolean isPriceValid(String price) {
        try {
            Double.parseDouble(price);
        } catch (Exception err) {
            return false;
        }
        return true;
    }

    private static boolean isVehicleValid(String vehicle) {
        switch (vehicle.toUpperCase()) {
            case "TRAIN":
            case "PLANE":
            case "BUS":
            case "BOAT":
                return true;
            default:
                return false;
        }
    }


    private static class Tours {
        LinkedList<Tour> tours = new LinkedList<Tour>();

        public Tours() {
            this.setTours();
        }

        public void setTours() {
            try {
                this.tours.clear();
                Scanner fileScan = new Scanner(new File("db.csv"));
                while (fileScan.hasNextLine()) {
                    String[] tourParams = fileScan.nextLine().split(";");
                    Tour tour = new Tour(
                            Integer.parseInt(tourParams[0]),
                            tourParams[1],
                            tourParams[2],
                            Integer.parseInt(tourParams[3]),
                            Double.parseDouble(tourParams[4]),
                            tourParams[5]
                    );
                    tours.add(tour);
                }
                fileScan.close();
            } catch (FileNotFoundException err) {
                err.printStackTrace();
            }
        }

        public LinkedList<Tour> getTours() {
            return this.tours;
        }
    }

    private static class Tour {
        int id;
        String city;
        String date;
        int days;
        double price;
        String vehicle;

        public Tour(
                int id,
                String city,
                String date,
                int days,
                double price,
                String vehicle
        ) {
            this.id = id;
            this.city = city;
            this.date = date;
            this.days = days;
            this.price = price;
            this.vehicle = vehicle;
        }

        public void print() {
            System.out.printf("%-4d", this.id);
            System.out.printf("%-21s", this.city);
            System.out.printf("%-11s", this.date);
            System.out.printf("%6d", this.days);
            System.out.printf("%10.2f", this.price);
            System.out.printf(" %-7s", this.vehicle);
        }

    }

}

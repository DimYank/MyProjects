package threads;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class Formatter{

    private ThreadLocal<SimpleDateFormat> format = new ThreadLocal();

    public String format(Date date){
        format.set(new SimpleDateFormat("dd-MM-yyyy"));
        return format.get().format(date);
    }
}

class FormatterThread extends Thread{

    private Formatter formatter;
    private Date date;

    public FormatterThread(Formatter formatter, Date date) {
        this.formatter = formatter;
        this.date = date;
    }

    @Override
    public void run() {
        System.out.println(formatter.format(date));
    }
}

public class Task13 {
    private Formatter formatter = new Formatter();
    private Date date = new Date();

    public void main(String[] args){
        date.setYear(2);
        date.setMonth(Calendar.FEBRUARY);
        date.setDate(2);
        Thread thread1 = new FormatterThread(formatter, date);
        Thread thread2 = new FormatterThread(formatter, date);
        Thread thread3 = new FormatterThread(formatter, date);
        Thread thread4 = new FormatterThread(formatter, date);
        Thread thread5 = new FormatterThread(formatter, date);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();

        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread5.join();
            thread4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Done.");

    }
}

package id.co.lua.pbj.penilaian_karyawan.schedule;


import lombok.SneakyThrows;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

@Component
public class ThreadPoolTaskImport {

    final ThreadPoolTaskScheduler taskScheduler;


    public ThreadPoolTaskImport(ThreadPoolTaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;

    }


    public void importFromCountry(InputStream ifs,Boolean isXlsx){
        taskScheduler.schedule(new RunnableTask( "[Import Location All]",ifs,isXlsx), new Date());
    }

    class RunnableTask implements Runnable {
        File file;
        private String message;
        private InputStream ifs;
        Boolean isXlsx;


        public RunnableTask(String message,InputStream ifs,Boolean isXlsx) {
            this.message=message;
            this.ifs = ifs;
            this.isXlsx=isXlsx;

        }

        @SneakyThrows
        @Override
        public void run() {
            System.out.println(new Date()+"Runnable Task with " + message + " on thread " + Thread.currentThread().getName());

        }
    }


    public void importDataSekolah(InputStream ifs,Boolean isXlsx){
        taskScheduler.schedule(new RunnableTaskSekolah( "[Import Data Sekolah]",ifs,isXlsx), new Date());
    }
    class RunnableTaskSekolah implements Runnable {
        File file;
        private String message;
        private InputStream ifs;
        Boolean isXlsx;


        public RunnableTaskSekolah(String message,InputStream ifs,Boolean isXlsx) {
            this.message=message;
            this.ifs = ifs;
            this.isXlsx=isXlsx;

        }

        @SneakyThrows
        @Override
        public void run() {
            System.out.println(new Date()+"Runnable Task with " + message + " on thread " + Thread.currentThread().getName());

        }
    }

    public void importDataAnggaranSekolah(InputStream ifs,Boolean isXlsx,int tahun){
        taskScheduler.schedule(new RunnableTaskAnggaranSekolah( "[Import Data Anggaran Sekolah]",ifs,isXlsx,tahun), new Date());
    }
    class RunnableTaskAnggaranSekolah implements Runnable {
        File file;
        private String message;
        private InputStream ifs;
        Boolean isXlsx;
        int tahun;


        public RunnableTaskAnggaranSekolah(String message,InputStream ifs,Boolean isXlsx,int tahun) {
            this.message=message;
            this.ifs = ifs;
            this.isXlsx=isXlsx;
            this.tahun=tahun;

        }

        @SneakyThrows
        @Override
        public void run() {
            System.out.println(new Date()+"Runnable Task with " + message + " on thread " + Thread.currentThread().getName());
            System.out.println("FINISH IMPORT Anggaran Sekolah");

        }
    }
}
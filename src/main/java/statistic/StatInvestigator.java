package statistic;

import org.hyperic.sigar.*;

public class StatInvestigator {

    private static Sigar sigar = new Sigar();

    public static double getSystemStatistics() throws SigarException {
//        Mem mem = null;
//        CpuPerc cpuperc = null;
//        FileSystemUsage filesystemusage = null;
//        try {
//            Cpu cpu = sigar.getCpu();
//            System.out.println("CPU: " + cpu.toString());
//            mem = sigar.getMem();
//            cpuperc = sigar.getCpuPerc();
//            filesystemusage = sigar.getFileSystemUsage("C:");
//        } catch (SigarException se) {
//            se.printStackTrace();
//        }
//
//        System.out.print("Mem " + mem.getUsedPercent() + "\t");
//        System.out.print("Cpu " + (cpuperc.getCombined() * 100) + "\t");
//        System.out.print("FileSystem " + filesystemusage.getUsePercent() + "\n");
//        System.out.println(cpuperc.toString());
//        System.out.println();
//        Mem mem = sigar.getMem();
//        System.out.println(mem.toString());
//
//        Swap swap = sigar.getSwap();
//        System.out.println(swap.toString());
//        System.out.println("Total swap in readable format: " + Sigar.formatSize(swap.getTotal()));
//
//        Cpu cpu = sigar.getCpu();
//        System.out.println("CPU: " + cpu.toString());

        CpuPerc cpuPerc = sigar.getCpuPerc();
//        System.out.println(cpuPerc.toString());
//        System.out.println(cpuPerc.getCombined());
//        System.out.println(1 - cpuPerc.getIdle());
//        System.out.println();
//        CpuInfo[] cpuInfo = sigar.getCpuInfoList();
//        for (CpuInfo temp : cpuInfo) {
//            System.out.println(temp.toString());
//        }
//
//        ResourceLimit rLimit = sigar.getResourceLimit();
//        // a range of values includes core, cpu, mem, opened files etc and depends on platform
//        System.out.println("ResourceLimit: " + rLimit.toString());
//
//        System.out.println("System uptime in seconds: " + sigar.getUptime().toString());
        return 1 - cpuPerc.getIdle();
    }

    public static void startStatistic(){
        Runnable r = new Runnable() {
            public void run() {
                StatInvestigator m1 = new StatInvestigator();
                for (int i = 0; i < 1000; ) {
                    try {
                        Thread.sleep(1000);
                        m1.getSystemStatistics();
                    } catch (SigarException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        new Thread(r).start();
    }

    public static void main(String[] args) {
        StatInvestigator.startStatistic();
        int a = 10;
        for(int i = 0; i < 100000; i++){
            if(a > 10){}
            if(a < 10){}
            if(a == 10){}
            a += 1;
            a -= 1;
            for(int j = 0; j < 100000; j++){
                a += 10;
                a -= 10;
                if(a > 10){}
                if(a < 10){}
                if(a == 10){}
                double iii = 1000.0*8.3/3.6 + 3.664 + 9.2/7.3;
                System.out.print("");
            }
        }

    }


}
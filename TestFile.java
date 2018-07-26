package com.lvmama.test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class TestFile {

    public static void main(String args[])throws IOException{
        File f=new File("E:\\hadoop-2.7.3.rar");
        System.out.println(getDirInfo(f.getAbsolutePath()));
    }

    public static String getDirInfo(String filePath) throws IOException{
        File f=new File(filePath);
        StringBuilder result= new StringBuilder();
        if(f.isDirectory()){
            for(File elem:f.listFiles()){
                FileStatisticInfo fileStatisticInfo=caculator(elem,false);
                result.append(elem.getName());
                result.append("(D): 目录数");
                result.append(fileStatisticInfo.getDirCount());
                result.append("，文件数");
                result.append(fileStatisticInfo.getFileCount());
                result.append("，总容量：[");
                result.append(fileStatisticInfo.getCapacity());
                result.append("]");
                result.append(fileStatisticInfo.getFormatCapacity());
            }
        }else {
            FileStatisticInfo fileStatisticInfo=caculator(f,false);
            result.append(f.getName());
            result.append("(F)： 大小:[");
            result.append(fileStatisticInfo.getCapacity());
            result.append("]");
            result.append(fileStatisticInfo.getFormatCapacity());
        }
        return result.toString();
    }

    /**
     * 计算一个目录下所有文件大小总和
     * @param inputFile  需要计算的文件或者目录
     * @param dirIncluded  是否计算目录占用磁盘空间，目录本身也占用磁盘空间
     * @return  目录大小，文件夹数量，文件数量
     * @throws IOException
     */
    public static FileStatisticInfo caculator(File inputFile,boolean dirIncluded) throws IOException {
        if(inputFile==null) return null;
        FileStatisticInfo fileStatisticInfo=new FileStatisticInfo();
        fileStatisticInfo.setFilePath(inputFile.getCanonicalPath());
        if(inputFile.isDirectory()){//如果需要计算的文件是目录, 则计算该目录下所有文件及目录大小总和
            for(File tempf:inputFile.listFiles()){
                if(tempf.isDirectory()) { //统计子目录信息
                    FileStatisticInfo tempfs=caculator(tempf, dirIncluded);
                    if(dirIncluded)
                        fileStatisticInfo.setCapacity(tempf.length()+fileStatisticInfo.getCapacity()+tempfs.getCapacity());
                    else
                        fileStatisticInfo.setCapacity(fileStatisticInfo.getCapacity()+tempfs.getCapacity());
                    fileStatisticInfo.setDirCount(1+tempfs.getDirCount()+fileStatisticInfo.getDirCount());
                    fileStatisticInfo.setFileCount(fileStatisticInfo.getFileCount()+tempfs.getFileCount());
                }
                else {
                    fileStatisticInfo.setCapacity(fileStatisticInfo.getCapacity()+tempf.length());
                    fileStatisticInfo.setFileCount(1+fileStatisticInfo.getFileCount());
                }
            }
        }else fileStatisticInfo.setCapacity(inputFile.length());
        return fileStatisticInfo;
    }
    static final class FileStatisticInfo{
        //绝对路径
        private String filePath;
        //包含文件数
        private int fileCount;
        //包含目录数
        private int dirCount;
        //包含文件大小总和
        private long capacity;

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public int getFileCount() {
            return fileCount;
        }

        public void setFileCount(int fileCount) {
            this.fileCount = fileCount;
        }

        public int getDirCount() {
            return dirCount;
        }

        public void setDirCount(int dirCount) {
            this.dirCount = dirCount;
        }

        public long getCapacity() {
            return capacity;
        }

        public void setCapacity(long capacity) {
            this.capacity = capacity;
        }

        /**
         * 把文件大小转换成易读形式
         * @return 返回字符串
         */
        public String getFormatCapacity(){
            if(capacity>>10<1){
                return capacity+"B";
            }else if(capacity>>20<1){
                return BigDecimal.valueOf(capacity).divide(BigDecimal.valueOf(1<<10),1,RoundingMode.HALF_UP)+"K";
            }else if(capacity>>30<1){
                return BigDecimal.valueOf(capacity).divide(BigDecimal.valueOf(1<<20),1,RoundingMode.HALF_UP)+"M";
            }else {
                return BigDecimal.valueOf(capacity).divide(BigDecimal.valueOf(1<<30),1,RoundingMode.HALF_UP)+"G";
            }
        }

        @Override
        public String toString() {
            return "FileStatisticInfo{" +
                    "filePath='" + filePath + '\'' +
                    ", fileCount=" + fileCount +
                    ", dirCount=" + dirCount +
                    ", capacity=" + capacity +
                    '}';
        }
    }
}

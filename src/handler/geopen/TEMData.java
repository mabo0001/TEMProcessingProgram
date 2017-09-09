/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handler.geopen;

import constant.geopen.ConstantPara;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.swing.JOptionPane;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import ui.geopen.TEMChartPanle;
import ui.geopen.TEMProcessingProgramWin;

/**
 *
 * @author Administrator
 */
public class TEMData {

    //创建主界面对话框
    private TEMProcessingProgramWin frame;
    //文件过滤器
    private TEMFileFilter fileFilter;
    //标记读取的是gptm文件还是tm文件
    public int flagFileType = -1;//0标示tm；1标示gptm 2标示已经积分 -1 标示出现错误 
    public File[] files = null;
    public static int flagGF = 0;//默认地面数据0

    public TEMData(TEMProcessingProgramWin frame) {
        this.frame = frame;//初始化界面
    }

    /**
     * 打开文件
     *
     * @param frame 主界面
     */
    public File[] openFileButtAction() {
        //读取文件
        File[] files = null;
        fileFilter = new TEMFileFilter(ConstantPara.fileAbsPath);
        fileFilter.setMultiSelectionEnabled(true);//设定可以选定多个文件
        if (fileFilter.showOpenDialog(frame) == TEMFileFilter.APPROVE_OPTION) {
            //初始化
            if (fileFilter.getSuf().equalsIgnoreCase("tm")) {
                initalParameters();
                try {
                    //抽取数据
                    files = read_extract_TEMData(fileFilter);//抽取数据
                    //文件类型
                    flagFileType = 0;//地面数据
                } catch (IOException ex) {
                    flagFileType = -1;
//                    File f = new File("Error.txt");
//                    try {
//                        FileWriter fileWriter = new FileWriter(f);
//                        fileWriter.write(ex.toString());
//                        fileWriter.close();
//                    } catch (IOException ex1) {
//                        Logger.getLogger(TEMData.class.getName()).log(Level.SEVERE, null, ex1);
//                    }
                    JOptionPane.showMessageDialog(frame, ex.toString());
                    return null;
                }
            } else if (fileFilter.getSuf().equalsIgnoreCase("usf")) {//当读取的是usf数据
            } else if (fileFilter.getSuf().equalsIgnoreCase("gptm")) {//当读取的是gptm数据
                //初始化
                initalParameters();
                try {
                    //读取文件
                    files = readGPTMFile();
                    //文件类型
                    flagFileType = 1;
                } catch (FileNotFoundException ex) {
                    flagFileType = -1;
                    JOptionPane.showMessageDialog(frame, "没有找到文件！");
                    return null;
                } catch (IOException ex) {
                    flagFileType = -1;
                    JOptionPane.showMessageDialog(frame, "读取文件错误！");
                    return null;
                } catch (ClassNotFoundException ex) {
                    flagFileType = -1;
                    JOptionPane.showMessageDialog(frame, "找不到相关类！");
                    return null;
                }
            } else {//读取的文件类型不对
                //文件类型
                flagFileType = -1;
            }
        }
        return files;
    }

    public void initalParameters() {
        //初始化图表
        if (frame.TEMChart_Panel != null) {
            XYPlot plot = (XYPlot) frame.TEMChart_Panel.chartPanel.getChart().getPlot();
            plot.clearAnnotations();
            TEMChartPanle.series1.clear();
            TEMChartPanle.series2.clear();
            TEMChartPanle.series3.clear();
            TEMChartPanle.series4.clear();
            TEMChartPanle.series5.clear();
        }
        //初始化TEMSourceData 里的集合参数
        TEMSourceData.xy_fileName.clear();
        TEMSourceData.z_fileName.clear();
        TEMSourceData.integrationValue.clear();
        TEMSourceData.inversionValue.clear();
        TEMSourceData.lineName_XYList.clear();
        TEMSourceData.lineName.clear();
        TEMSourceData.temData = null;
        //组件清理
        frame.pointsPositionPanel.removeAll();
        if (frame.originalDataPanel.getComponents().length != 0) {
            frame.originalDataPanel.removeAll();
            frame.dataVisualTabbedPane.remove(1);
        }
        //标签清理
        frame.totalFilesLabel.setText("文件总数：");
        frame.fileNameLabel.setText("当前文件名：");
        frame.posLabel.setText("数据点坐标：");
        frame.repaint();
    }

    public File[] readGPTMFile() throws FileNotFoundException, IOException, ClassNotFoundException {
        File file = fileFilter.getSelectedFile();
        fileFilter.setCurrentDirectory(file);
        //获得上一次文件的路径
        ConstantPara.fileAbsPath = file.getAbsolutePath();
        ConstantPara.fileCurrentDir = file.getParent();//获得文件夹
        ConstantPara.fileAbsPath = ConstantPara.fileAbsPath.replaceAll("\\\\", "/");
        //建立文件流
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        //赋值
        TEMSourceData.filesName = (String[]) ois.readObject();//文件名
        TEMSourceData.workPlace = (String[]) ois.readObject();//工作地点
        TEMSourceData.latitude = (String[]) ois.readObject();//纬度
        TEMSourceData.longtitude = (String[]) ois.readObject();//经度
        TEMSourceData.status = (String[]) ois.readObject();//定位状态
        TEMSourceData.time = (String[]) ois.readObject();//采样时间
        TEMSourceData.gain = (int[]) ois.readObject();//增益
        TEMSourceData.channels = (int[]) ois.readObject();//道数
        TEMSourceData.fundfrequency = (int[]) ois.readObject();//基频
        TEMSourceData.superposition = (int[]) ois.readObject();//叠加次数
        //初始化坐标设置参数
        TEMSourceData.trWidth = (Object[]) ois.readObject();//Tx边长X(m)
        TEMSourceData.trLength = (Object[]) ois.readObject();//Tx边长Y(m)
        TEMSourceData.trCenterX = (Object[]) ois.readObject();
        TEMSourceData.trCenterY = (Object[]) ois.readObject();
        TEMSourceData.trAngle = (Object[]) ois.readObject();
        TEMSourceData.trTurns = (Object[]) ois.readObject();
        TEMSourceData.rCenterX = (Object[]) ois.readObject();
        TEMSourceData.rCenterY = (Object[]) ois.readObject();
        TEMSourceData.rCenterZ = (Object[]) ois.readObject();
        TEMSourceData.rLongtitude = (Object[]) ois.readObject();
        TEMSourceData.rLatitude = (Object[]) ois.readObject();
        TEMSourceData.rArea = (Object[]) ois.readObject();
        TEMSourceData.Array = (Object[]) ois.readObject();
        TEMSourceData.turnOffTime = (Object[]) ois.readObject();
        TEMSourceData.current = (Object[]) ois.readObject();
        TEMSourceData.firstChannel = (Object[]) ois.readObject();//第一道
        TEMSourceData.secondChannel = (Object[]) ois.readObject();//第二道
        TEMSourceData.thirdChannel = (Object[]) ois.readObject();//第二道
        //积分 显示参数
        TEMSourceData.z_fileName = (HashMap<String, Double>) ois.readObject();//自定义高程String_文件名，list坐标值，用于保存文件
        TEMSourceData.xy_fileName = (HashMap<ArrayList, String>) ois.readObject();//自定义的大地坐标String_文件名，list坐标值
        TEMSourceData.integrationValue = (HashMap<String, ArrayList>) ois.readObject();//自定义的String_文件名，ArrayList对应的积分数据 包括电压、中间时间等
        TEMSourceData.lineName_XYList = (HashMap<String, ArrayList>) ois.readObject();//定义测线 对应的坐标 ArrayList 所选的点的坐标
        TEMSourceData.lineName = (ArrayList<String>) ois.readObject();//测线名
        TEMSourceData.temData = (double[][][]) ois.readObject();//源数据
//        TEMSourceData.inversionValue = (HashMap<String, ArrayList>) ois.readObject();//自定义的String_文件名，单点反演数据
        ois.close();
        //创建文件 赋值给tree
        files = new File[TEMSourceData.filesName.length];
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(ConstantPara.fileCurrentDir + "\\" + TEMSourceData.filesName[i]);
        }
        //显示坐标
        HashSet<Double> pos = new HashSet<Double>();
        int rowCount = files.length;
        for (int i = 0; i < files.length; i++) {
            double x = Double.parseDouble(TEMSourceData.rCenterX[i].toString());
            double y = Double.parseDouble(TEMSourceData.rCenterY[i].toString());
            TEMChartPanle.series1.add(x, y);//series1只负责绘制边框
            double posxy = x * 0.00001 + y;
            pos.add(posxy);
        }
        //判断是否有相同坐标点
//        if (pos.size() != rowCount) {
//            JOptionPane.showMessageDialog(frame, "坐标存在相同的点，将不予显示，请修改坐标！");
//            return files;
//        }
        //增加坐标面板
        addTEMChartPanel();
        return files;
    }

    public void addTEMChartPanel() {
        //将图表添加到主界面内
        frame.pointsPositionPanel.removeAll();
        frame.TEMChart_Panel = new TEMChartPanle(frame, TEMSourceData.Array[0].toString());
        //设定样式
        frame.pointsPositionPanel.add(frame.TEMChart_Panel);
        frame.repaint();//重绘才会出现坐标显示
    }

    private class IntegerCompartor implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            try {
                Integer int1 = Integer.parseInt(o1.getName().split("[.]")[0]);
                Integer int2 = Integer.parseInt(o2.getName().split("[.]")[0]);
                return int1.compareTo(int2);
            } catch (Exception e) {
                return 0;
            }
        }
    }

    /**
     * 读取TEM文件 初始化数据组
     *
     * @param filefilter
     */
    public File[] read_extract_TEMData(TEMFileFilter fileFilter) throws IOException {

        File file = fileFilter.getSelectedFile();
        fileFilter.setCurrentDirectory(file);
        //获得上一次文件的路径
        ConstantPara.fileAbsPath = file.getAbsolutePath();
        ConstantPara.fileCurrentDir = file.getParent();//获得文件夹
        ConstantPara.fileAbsPath = ConstantPara.fileAbsPath.replaceAll("\\\\", "/");
        //获得所有文件
        File[] files = fileFilter.getSelectedFiles();
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new IntegerCompartor());
        files = (File[]) fileList.toArray();
        String path = "";
        if (flagGF == 1) {
            path = splitFiles(files);
            this.files = new File(path).listFiles();
        } else {
            this.files = files;
        }
        boolean hasGPS = firstJustifiedFileType(this.files);//初始化数组

        boolean error;
        if (hasGPS == true) {//老文件
            error = readFiles(this.files, path);//读取文件数据
        } else {//新文件
            error = read_HighLowFile(this.files);
        }
        if (error == false) {
            return null;
        }
        return this.files;
    }

    public String extractHeadInfor(RandomAccessFile raf, int offset) throws IOException {
        String infor = "";
        raf.skipBytes(8);//跳出装置形式
        byte[] pos = new byte[128];//工作地点
        raf.read(pos);
        int recPos = 0;
        for (int i = 0; i < pos.length; i++) {
            byte b = pos[i];
            if (b != 0) {
                recPos = i;
            }
        }
        byte[] posSub = new byte[recPos + 1];//删去空格 只存地点
        for (int i = 0; i <= recPos; i++) {
            posSub[i] = pos[i];
        }
        String workPos = new String(posSub, "GBK");
//        TEMAcquisitionProMain.daPosPath.setText(workPos);
        byte[] gpsInf = new byte[32];//经纬度
        raf.read(gpsInf);//读取gps 义
        try {
            String gpsInfStr = new String(gpsInf);
            String[] splitStrs = gpsInfStr.split(",");
            //截取纬度
            String[] latitudeSplit = splitStrs[1].split("[.]");
//            System.out.println(gpsInfStr + "," + latitudeSplit.length);
//            String pointLat = latitudeSplit[0].substring(latitudeSplit[0].length() - 2, latitudeSplit[0].length());
//            String degreeLat = latitudeSplit[0].substring(1, latitudeSplit[0].length() - 2);
//            String concat = pointLat + "." + latitudeSplit[1];
//            double pointLatV = Double.parseDouble(concat);

//            double pointLatVV = Double.parseDouble(concat) / 60.0;
//            double degreeLatVV = Double.parseDouble(degreeLat) + pointLatVV;

            //经度
//            String[] longitudeSplit = splitStrs[0].split("[.]");
//            String pointLong = longitudeSplit[0].substring(longitudeSplit[0].length() - 2, longitudeSplit[0].length());
//            String degreeLong = longitudeSplit[0].substring(1, longitudeSplit[0].length() - 2);
//            String concatLong = pointLong + "." + longitudeSplit[1];
//            double pointLongV = Double.parseDouble(concatLong);
//            double pointLonVV = Double.parseDouble(concatLong) / 60.0;
//            double degreeLonVV = Double.parseDouble(degreeLong) + pointLonVV;
        } catch (Exception e) {
            e.printStackTrace();
//            JOptionPane.showMessageDialog(frame, "GPS信息有误！");
//            TEMAcquisitionProMain.latitudeFormat.setText("无定位");
//            TEMAcquisitionProMain.longitudeFormat.setText("无定位");
//            TEMAcquisitionProMain.locationState.setText("NO");
        }
        byte[] gpsState = new byte[2];//定位ok no
        raf.read(gpsState);//定位状态
        String gpsStateStr = new String(gpsState);
//        TEMAcquisitionProMain.locationState.setText(gpsStateStr);
        byte[] fileName = new byte[16];//文件名
        raf.read(fileName);
        byte[] gpsTime = new byte[19];//记录时间
        raf.read(gpsTime);
        String gpsTimeStr = new String(gpsTime);
        System.out.println(gpsTimeStr);
        String[] gpsTimeStrs = gpsTimeStr.split(" ");
        String[] gpsTimeDateStrs = gpsTimeStrs[0].split(".");
//        GPSDialog.dateFormat.setText(gpsTimeDateStrs[0]+"年"+gpsTimeDateStrs[1]+"月"+gpsTimeDateStrs[2]+"日");
//        TEMAcquisitionProMain.dateFormat.setText(gpsTimeStrs[0]);
//        TEMAcquisitionProMain.timeFormat.setText(gpsTimeStrs[1]);

//        System.out.println(gpsTimeStrs[0]);
//        System.out.println(gpsTimeStrs[1]);
//        System.out.println(gpsTimeStr);
        infor = gpsTimeStrs[0].replace(".", "") + gpsTimeStrs[1].replace(":", "");
        raf.readShort();//读取增益
//        TEMAcquisitionProMain.gSpinner.setValue(Parameters.gain);
        raf.read();//读取道数
//        TEMAcquisitionProMain.channelSpinner.setValue(Parameters.channels);
        byte[] channelEH = new byte[32];//通道定义
        raf.read(channelEH);
        String channelEHStr = new String(channelEH);
        String[] unitChannelEH = channelEHStr.trim().split("[/]");
        String[] channel1_EH = unitChannelEH[0].split("=");
        String[] channel2_EH = unitChannelEH[1].split("=");
        String[] channel3_EH = unitChannelEH[2].split("=");
//        Parameters.channelsEH = new String[3];
//        Parameters.channelsEH[0] = channel1_EH[1].substring(0, 2);
//        Parameters.channelsEH[1] = channel2_EH[1].substring(0, 2);
//        Parameters.channelsEH[2] = channel3_EH[1].substring(0, 2);
        raf.read();//基频号 求采样长度
        raf.readShort();//获得叠加次数
        raf.readShort();//获得站点编号
        raf.skipBytes(7);//按键标示1 电阻6 
        raf.read();//抑制频率
        raf.skipBytes(offset + 512 - (int) raf.getFilePointer());//跳出文件头
        return infor;
    }

    public String makeFileDir(File file) {
        File[] roots = File.listRoots();//获得盘符
        File dir = new File(roots[0].toString().split("[:]")[0] + ":\\" + file.getName().split("[.]")[0]);
        dir.mkdir();
        String path = dir.getAbsolutePath();
        return path;
    }

    /**
     * 分割文件
     */
    public String splitFiles(File[] files) throws FileNotFoundException, IOException {
        //分割文件
        RandomAccessFile raf = null;
        String path = "";
        byte[] buffer;
        for (int i = 0; i < files.length; i++) {
            path = makeFileDir(files[i]);//生成文件夹
            raf = new RandomAccessFile(files[i], "rw");
            int fileLength = (int) files[i].length();
            buffer = new byte[fileLength];
            FileInputStream fis = new FileInputStream(files[i]);
            DataInputStream dis = new DataInputStream(fis);
            dis.read(buffer);
            dis.close();
            for (int k = 0; k < 1000000; k++) {
                if (k * 7168 > raf.length()) {
                    break;
                }
                raf.seek(0);
                raf.seek(k * 7168);
                String station = files[i].getName().substring(0, 5);
                String path2 = path + "\\" + station + extractHeadInfor(raf, k * 7168) + ".TM";
                File file1 = new File(path2);
                FileOutputStream fos = new FileOutputStream(file1);
                DataOutputStream dos = new DataOutputStream(fos);
                if (k - 1 < 0) {
                    dos.write(Arrays.copyOfRange(buffer, 0, 7168));
                } else {
                    dos.write(Arrays.copyOfRange(buffer, (k - 1) * 7168, k * 7168));
                }
                dos.close();
            }
        }
        return path;
    }

    public boolean firstJustifiedFileType(File[] files1) throws FileNotFoundException, IOException {
        RandomAccessFile raf = null;
        int length = files1.length;
        //存储所有文件的GPS信息
        TEMSourceData.filesName = new String[length];//文件名
        TEMSourceData.workPlace = new String[length];//工作地点
        TEMSourceData.latitude = new String[length];;//纬度
        TEMSourceData.longtitude = new String[length];;//经度
        TEMSourceData.status = new String[length];;//定位状态
        TEMSourceData.time = new String[length];;//采样时间
        TEMSourceData.gain = new int[length];;//增益
        TEMSourceData.channels = new int[length];;//道数
        TEMSourceData.fundfrequency = new int[length];//基频
        TEMSourceData.superposition = new int[length];//叠加次数
        //初始化坐标设置参数
        TEMSourceData.trWidth = new Object[length];//Tx边长X(m)
        TEMSourceData.trLength = new Object[length];//Tx边长Y(m)
        TEMSourceData.trCenterX = new Object[length];
        TEMSourceData.trCenterY = new Object[length];
        TEMSourceData.trAngle = new Object[length];
        TEMSourceData.trTurns = new Object[length];
        TEMSourceData.rCenterX = new Object[length];
        TEMSourceData.rCenterY = new Object[length];
        TEMSourceData.rCenterZ = new Object[length];
        TEMSourceData.rLongtitude = new Object[length];
        TEMSourceData.rLatitude = new Object[length];
        TEMSourceData.rArea = new Object[length];
        TEMSourceData.Array = new Object[length];
        TEMSourceData.turnOffTime = new Object[length];
        TEMSourceData.current = new Object[length];
        TEMSourceData.firstChannel = new Object[length];//第一道
        TEMSourceData.secondChannel = new Object[length];//第二道
        TEMSourceData.thirdChannel = new Object[length];//第二道
//        DecimalFormat format = new DecimalFormat("0.0000000");
        for (int i = 0; i < length; i++) {
            raf = new RandomAccessFile(files1[i], "rw");
            raf.skipBytes(8);//跳出装置形式
            //*******检查GPS数据有无问题*********************
            try {
                byte[] pos = new byte[128];//工作地点
                raf.read(pos);
                int recPos = 0;
                for (int j = 0; j < pos.length; j++) {
                    byte b = pos[j];
                    if (b != 0) {
                        recPos = j;
                    }
                }
                byte[] posSub = new byte[recPos + 1];//删去空格 只存地点
                for (int j = 0; j <= recPos; j++) {
                    posSub[j] = pos[j];
                }
                byte[] gpsInf = new byte[32]; //经纬度
                raf.read(gpsInf);//读取gps 义
                String gpsInfStr = new String(gpsInf).trim();
//                if (gpsInfStr.equalsIgnoreCase("")) {
////                    System.out.println(gpsInfStr.trim().equalsIgnoreCase(""));
//                    return false;
//                }
                String[] splitStrs = gpsInfStr.split(",");
                //截取纬度
//                String[] latitudeSplit = splitStrs[1].split("[.]");
//                String pointLat = latitudeSplit[0].substring(latitudeSplit[0].length() - 2, latitudeSplit[0].length());
//                String degreeLat = latitudeSplit[0].substring(1, latitudeSplit[0].length() - 2);
//                String concat = pointLat + "." + latitudeSplit[1];
//                double pointLatV = Double.parseDouble(concat);
//                System.out.println(degreeLat + "°" + pointLatV + "' " + splitStrs[1].substring(0, 1) + "*****");
//                TEMSourceData.latitude[i] = degreeLat + "°" + pointLatV + "' " + splitStrs[1].substring(0, 1);
                //经度
                String[] longitudeSplit = splitStrs[0].split("[.]");
                String pointLong = longitudeSplit[0].substring(longitudeSplit[0].length() - 2, longitudeSplit[0].length());
                String degreeLong = longitudeSplit[0].substring(1, longitudeSplit[0].length() - 2);
                String concatLong = pointLong + "." + longitudeSplit[1];
                double pointLongV = Double.parseDouble(concatLong);
                byte[] gpsState = new byte[2];//定位ok no
                raf.read(gpsState);//定位状态
                String gpsStateStr = new String(gpsState);
//                TEMSourceData.status[i] = gpsStateStr;
                byte[] fileName = new byte[16];//文件名
                raf.read(fileName);
                byte[] gpsTime = new byte[19];//记录时间
                raf.read(gpsTime);

                String gpsTimeStr = new String(gpsTime);
                String[] gpsTimeStrs = gpsTimeStr.split(" ");
                String[] gpsTimeDateStrs = gpsTimeStrs[0].split(".");
//                TEMSourceData.time[i] = gpsTimeStrs[0].concat("_").concat(gpsTimeStrs[1]);

                TEMSourceData.gain[i] = raf.readShort();//读取增益
                TEMSourceData.channels[i] = raf.read();//读取道数
//                switch (TEMSourceData.channels[i]) {
//                    case 1:
//                        TEMSourceData.firstChannel[i] = true;
//                        TEMSourceData.secondChannel[i] = null;
//                        TEMSourceData.thirdChannel[i] = null;
//                        break;
//                    case 2:
//                        TEMSourceData.firstChannel[i] = true;
//                        TEMSourceData.secondChannel[i] = true;
//                        TEMSourceData.thirdChannel[i] = null;
//                        break;
//                    case 3:
//                        TEMSourceData.firstChannel[i] = true;
//                        TEMSourceData.secondChannel[i] = true;
//                        TEMSourceData.thirdChannel[i] = true;
//                        break;
//                }
                raf.skipBytes(32);
                TEMSourceData.fundfrequency[i] = raf.read();//基频号 求采样长度
                System.out.println(TEMSourceData.fundfrequency[i]);
                if (TEMSourceData.fundfrequency[i] >= 5) {
                    return false;
                }
//                SpinnerNumberModel modelendDep = (SpinnerNumberModel) frame.paraDialog.timeWins.getModel();
//                modelendDep.setMaximum(TEMSourceData.fundfrequency[i] * 300 / 5);
//                double dividend = -1;//被除数 根据基频大小 25-16 12.5-8....
//                switch (TEMSourceData.fundfrequency[i]) {
//                    case 1:
//                        TEMSourceData.fundfrequency[i] = 1;
//                        dividend = 25;
//                        break;
//                    case 2:
//                        TEMSourceData.fundfrequency[i] = 2;
//                        dividend = 12.5;
//                        break;
//                    case 3:
//                        TEMSourceData.fundfrequency[i] = 3;
//                        dividend = 6.25;
//                        break;
//                    case 4:
//                        TEMSourceData.fundfrequency[i] = 4;
//                        dividend = 3.125;
//                        break;
//                }
//                if (dividend == -1) {
//                    JOptionPane.showMessageDialog(null, files[i].getName() + "叠加次数不存在");
//                }
//                判定文件是不是同一频率采集的
//                if (i > 0) {
//                    if (TEMSourceData.fundfrequency[i] != TEMSourceData.fundfrequency[i - 1]) {
//                        JOptionPane.showMessageDialog(null, "所选文件基频不全相同：" + TEMSourceData.filesName[i]);
//                        return false;
//                    }
//                }
//                TEMSourceData.superposition[i] = raf.readShort();//获得叠加次数
//                raf.skipBytes(512 - (int) raf.getFilePointer());//跳出文件头
//                //初始化其他的值默认为0D
//                TEMSourceData.trWidth[i] = 1D;//Tx边长X(m)
//                TEMSourceData.trLength[i] = 1D;//Tx边长Y(m)
//                TEMSourceData.trCenterX[i] = 0D;
//                TEMSourceData.trCenterY[i] = 0D;
//                TEMSourceData.trAngle[i] = 0D;
//                TEMSourceData.trTurns[i] = 1D;
//                //经纬度转换
//                double pointLongVv = Double.parseDouble(concatLong) / 60;
//                double degreeLongV = Double.parseDouble(degreeLong);
//                double pointLatVv = Double.parseDouble(concat) / 60;
//                double degreeLatV = Double.parseDouble(degreeLat);
//                double log = degreeLongV + pointLongVv;
//                double lat = degreeLatV + pointLatVv;
////                double log = Double.parseDouble(format.format(degreeLongV + pointLongVv).toString());
////                double lat = Double.parseDouble(format.format(degreeLatV + pointLatVv).toString());
//                double[] xyPos = GaussToBLToGauss.GaussToBLToGauss(log, lat);
//
//                TEMSourceData.rCenterX[i] = Double.parseDouble(format.format(xyPos[0]));
//                TEMSourceData.rCenterY[i] = Double.parseDouble(format.format(xyPos[1]));
//                TEMSourceData.rCenterZ[i] = 0D;
//                TEMSourceData.rArea[i] = 1D;
//                TEMSourceData.turnOffTime[i] = 0D;
//                TEMSourceData.current[i] = 1D;
            } catch (Exception e) {
//                e.printStackTrace();
//                JOptionPane.showMessageDialog(frame, "文件：" + files1[i].getName() + "GPS信息存在问题，请检查！");
//                initalParameters();
                return false;
            }
            raf.close();
        }
        return true;
    }

    /**
     * 读取高速低速采集数据 512扇区
     *
     * @param files1
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public boolean read_HighLowFile(File[] files1) throws FileNotFoundException, IOException {
//        String path = "";
//        if (flagGF == 1) {
//            path = splitFiles(files1);
//            files = new File(path).listFiles();
//        } else {
//            files = files1;
//        }
        RandomAccessFile raf = null;
//        //存储所有文件的GPS信息
        int length = files1.length;
//        TEMSourceData.filesName = new String[length];//文件名
//        TEMSourceData.workPlace = new String[length];//工作地点
//        TEMSourceData.latitude = new String[length];;//纬度
//        TEMSourceData.longtitude = new String[length];;//经度
//        TEMSourceData.status = new String[length];;//定位状态
//        TEMSourceData.time = new String[length];;//采样时间
//        TEMSourceData.gain = new int[length];;//增益
//        TEMSourceData.channels = new int[length];;//道数
//        TEMSourceData.fundfrequency = new int[length];//基频
//        TEMSourceData.superposition = new int[length];//叠加次数
        TEMSourceData.division = new double[length];//实际基频值
//        //初始化坐标设置参数
//        TEMSourceData.trWidth = new Object[length];//Tx边长X(m)
//        TEMSourceData.trLength = new Object[length];//Tx边长Y(m)
//        TEMSourceData.trCenterX = new Object[length];
//        TEMSourceData.trCenterY = new Object[length];
//        TEMSourceData.trAngle = new Object[length];
//        TEMSourceData.trTurns = new Object[length];
//        TEMSourceData.rCenterX = new Object[length];
//        TEMSourceData.rCenterY = new Object[length];
//        TEMSourceData.rCenterZ = new Object[length];
//        TEMSourceData.rLongtitude = new Object[length];
//        TEMSourceData.rLatitude = new Object[length];
//        TEMSourceData.rArea = new Object[length];
//        TEMSourceData.Array = new Object[length];
//        TEMSourceData.turnOffTime = new Object[length];
//        TEMSourceData.current = new Object[length];
//        TEMSourceData.firstChannel = new Object[length];//第一道
//        TEMSourceData.secondChannel = new Object[length];//第二道
//        TEMSourceData.thirdChannel = new Object[length];//第二道
        //源数据
        TEMSourceData.temData = new double[length][][];
        DecimalFormat format = new DecimalFormat("0.0000000");
        int cutCounts = 0;//去掉前几个点
        for (int i = 0; i < files.length; i++) {
//            System.out.println(files1[i].getName());
            raf = new RandomAccessFile(files[i], "rw");
            raf.skipBytes(8);//跳出装置形式
            TEMSourceData.Array[i] = "中心回线";
            byte[] pos = new byte[128];//工作地点
            raf.read(pos);
            int recPos = 0;
            for (int j = 0; j < pos.length; j++) {
                byte b = pos[j];
                if (b != 0) {
                    recPos = j;
                }
            }
            byte[] posSub = new byte[recPos + 1];//删去空格 只存地点

            for (int j = 0; j <= recPos; j++) {
                posSub[j] = pos[j];
            }

            TEMSourceData.filesName[i] = files[i].getName();//文件名
            TEMSourceData.workPlace[i] = new String(posSub, "GBK");//工作地点
            raf.skipBytes(32);//跳过没有用的参数 经纬度
            TEMSourceData.latitude[i] = "null";
            TEMSourceData.longtitude[i] = "null";
            byte[] gpsState = new byte[2];//定位ok no
            raf.read(gpsState);//定位状态
            String gpsStateStr = new String(gpsState);
            TEMSourceData.status[i] = gpsStateStr;

            raf.skipBytes(16);//跳过没有用的参数 文件名

            byte[] gpsTime = new byte[19];//记录时间
            raf.read(gpsTime);
            String gpsTimeStr = new String(gpsTime);
            String[] gpsTimeStrs = gpsTimeStr.split(" ");
            String[] gpsTimeDateStrs = gpsTimeStrs[0].split(".");
            TEMSourceData.time[i] = gpsTimeStrs[0].concat("_").concat(gpsTimeStrs[1]);
            TEMSourceData.gain[i] = raf.readShort();//读取增益
            TEMSourceData.channels[i] = raf.read();//读取道数
            switch (TEMSourceData.channels[i]) {
                case 1:
                    TEMSourceData.firstChannel[i] = true;
                    TEMSourceData.secondChannel[i] = null;
                    TEMSourceData.thirdChannel[i] = null;
                    break;
                case 2:
                    TEMSourceData.firstChannel[i] = true;
                    TEMSourceData.secondChannel[i] = true;
                    TEMSourceData.thirdChannel[i] = null;
                    break;
                case 3:
                    TEMSourceData.firstChannel[i] = true;
                    TEMSourceData.secondChannel[i] = true;
                    TEMSourceData.thirdChannel[i] = true;
                    break;
            }
            raf.skipBytes(32);//通道定义
            TEMSourceData.fundfrequency[i] = raf.read();//基频号 求采样长度
            TEMSourceData.superposition[i] = raf.readShort();//获得叠加次数
            raf.readShort();//获得站点编号
            raf.skipBytes(7);//按键标示1 电阻6 
            raf.read();//抑制频率
            //设置区分参数
            TEMSourceData.division[i] = setDivision(TEMSourceData.fundfrequency[i], 0);
//            System.out.println(TEMSourceData.fundfrequency[i] + "," + TEMSourceData.division[i] + "+++");
            //判定文件是不是同一频率采集的
            if (i > 0) {
                if (TEMSourceData.fundfrequency[i] != TEMSourceData.fundfrequency[i - 1]) {
                    JOptionPane.showMessageDialog(null, "所选文件基频不全相同：" + TEMSourceData.filesName[i] + ", 检查文件是否按照同一基频采集！");
                    files = Arrays.copyOfRange(files1, 0, i);
                    return false;
                }
            }
            raf.skipBytes(512 - (int) raf.getFilePointer());//跳出文件头
            if (TEMSourceData.division[i] == -1) {
                JOptionPane.showMessageDialog(null, files[i].getName() + " 叠加次数不存在,数据头存在错误！");
                continue;
            }
            if (TEMSourceData.fundfrequency[i] <= 2) {
                TEMSourceData.temData[i] = new double[TEMSourceData.channels[i]][TEMSourceData.fundfrequency[i] * 300 - cutCounts];//去掉前两个点 
            } else if (TEMSourceData.fundfrequency[i] <= 4 && TEMSourceData.fundfrequency[i] > 2) {
                TEMSourceData.temData[i] = new double[TEMSourceData.channels[i]][1200 - cutCounts];
            } else if (TEMSourceData.fundfrequency[i] == 5) {//25Hz高
                TEMSourceData.temData[i] = new double[TEMSourceData.channels[i]][5000 - cutCounts];
            } else if (TEMSourceData.fundfrequency[i] == 6) {//50Hz高
                TEMSourceData.temData[i] = new double[TEMSourceData.channels[i]][2500 - cutCounts];
            } else if (TEMSourceData.fundfrequency[i] == 7) {//100Hz高
                TEMSourceData.temData[i] = new double[TEMSourceData.channels[i]][1250 - cutCounts];
            }
            //初始化其他的值默认为0D
            TEMSourceData.trWidth[i] = 1D;//Tx边长X(m)
            TEMSourceData.trLength[i] = 1D;//Tx边长Y(m)
            TEMSourceData.trCenterX[i] = 0D;
            TEMSourceData.trCenterY[i] = 0D;
            TEMSourceData.trAngle[i] = 0D;
            TEMSourceData.trTurns[i] = 1D;
            //经纬度转换
//            double pointLongVv = Double.parseDouble("0") / 60;
//            double degreeLongV = Double.parseDouble("0");
//            double pointLatVv = Double.parseDouble("0") / 60;
//            double degreeLatV = Double.parseDouble("0");
//            double log = degreeLongV + pointLongVv;
//            double lat = degreeLatV + pointLatVv;
//            double[] xyPos = GaussToBLToGauss.GaussToBLToGauss(log, lat);

            double[] xyPos = new double[]{0, 0};
            TEMSourceData.rCenterX[i] = Double.parseDouble(format.format(xyPos[0]));
            TEMSourceData.rCenterY[i] = Double.parseDouble(format.format(xyPos[1]));
            TEMSourceData.rCenterZ[i] = 0D;
            TEMSourceData.rArea[i] = 1D;
            TEMSourceData.turnOffTime[i] = 0D;
            TEMSourceData.current[i] = 1D;


            //数据提取
            //保存文件
//            File f = new File(files[i].getName().split("[.]")[0] + ".txt");
//            FileWriter fw = new FileWriter(f);
//            if (TEMSourceData.fundfrequency[i] >= 5) {
//                fw.write(frame.changeFundFre(TEMSourceData.fundfrequency[i]) + "," + 500000 + "\n");//保存数据
//            } else if (TEMSourceData.fundfrequency[i] == 4) {
//                fw.write(frame.changeFundFre(TEMSourceData.fundfrequency[i]) + "," + 15000 + "\n");//保存数据
//            } else {
//                fw.write(frame.changeFundFre(TEMSourceData.fundfrequency[i]) + "," + 30000 + "\n");//保存数据
//            }
            try {
                XYSeries[] xyseries = new XYSeries[TEMSourceData.channels[i]];
                for (int j = 0; j < TEMSourceData.channels[i]; j++) {
                    xyseries[j] = new XYSeries("时间/电压", true);
                }
                int length1 = TEMSourceData.temData[i][0].length;
                for (int j = 0; j < length1; j++) {//采样点数
//                    if (j == 0) {//每道跳出前两个数据 需要乘以通道数
//                        raf.skipBytes(cutCounts * 4 * TEMSourceData.temData[i].length);
//                    }
//                    double values[] = new double[TEMSourceData.channels[i]];
                    for (int m = 0; m < TEMSourceData.temData[i].length; m++) {//通道数
                        //读取数据
                        try {
                            if (TEMSourceData.fundfrequency[i] >= 5) {//25Hz 高速采集
//                                TEMSourceData.temData[i][m][j] = 8192 * (raf.readInt() * 1.0 / TEMSourceData.gain[i] / Math.pow(2, 18));//毫伏
                                double value = 8192 * (raf.readInt() * 1.0 / TEMSourceData.gain[i] / Math.pow(2, 18));//伏特
//                                values[m] = value;//保存数值
                                xyseries[m].add((j + 1) * 1000.0 / 500000 - 0.001, value);
                                if (j >= 0 && j < length1 - 1) {
                                    interplationXYSeries(xyseries[m], 0);
                                } else if (j == length1 - 1) {
                                    interplationXYSeries(xyseries[m], 1);
                                }
                            } else {
                                TEMSourceData.temData[i][m][j] = 10000 * (raf.readInt() * 1.0 / TEMSourceData.gain[i] / Math.pow(2, 24));//毫伏
//                                values[m] = TEMSourceData.temData[i][m][j];//保存数值
//                                TEMSourceData.temData[i][m][j] = raf.readInt() * 1.0 / TEMSourceData.gain[i];
                            }
                        } catch (Exception e) {
                            TEMSourceData.temData[i][m][j] = 0;//null
//                            values[m] = 0;
                        }
                    }
//                    if (j < length1 - 1) {
//                        FilesToTxt.savePara(fw, values, false);
//                    } else {
//                        FilesToTxt.savePara(fw, values, true);
//                    }
                }
                //更改积分时窗范围
                changeIntegerWins(i, xyseries);
//                if (TEMSourceData.fundfrequency[i] >= 5) {//25Hz 高
//                    for (int m = 0; m < TEMSourceData.temData[i].length; m++) {//通道
//                        TEMSourceData.temData[i][m] = xyseries[m].toArray()[1];
//                    }
//                    frame.paraDialog.startTSpinner.setValue(0.001);
//                    if (TEMSourceData.fundfrequency[i] == 5) {
//                        frame.paraDialog.endTSpinner.setValue(10);
//                    } else if (TEMSourceData.fundfrequency[i] == 6) {
//                        frame.paraDialog.endTSpinner.setValue(5);
//                    } else if (TEMSourceData.fundfrequency[i] == 7) {
//                        frame.paraDialog.endTSpinner.setValue(2.5);
//                    }
//                } else {
//                    if (TEMSourceData.fundfrequency[i] == 1) {
//                        frame.paraDialog.startTSpinner.setValue(0.033);
//                        frame.paraDialog.endTSpinner.setValue(10);
//                    } else if (TEMSourceData.fundfrequency[i] == 2) {
//                        frame.paraDialog.startTSpinner.setValue(0.033);
//                        frame.paraDialog.endTSpinner.setValue(20);
//                    } else if (TEMSourceData.fundfrequency[i] == 3) {
//                        frame.paraDialog.startTSpinner.setValue(0.033);
//                        frame.paraDialog.endTSpinner.setValue(40);
//                    } else if (TEMSourceData.fundfrequency[i] == 4) {
//                        frame.paraDialog.startTSpinner.setValue(0.067);
//                        frame.paraDialog.endTSpinner.setValue(80);
//                    }
//                }
                //清理
                for (int j = 0; j < TEMSourceData.channels[i]; j++) {
                    xyseries[j].clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "源文件数据量不够！显示存在问题，请检查源数据！");
                return false;
            }
            raf.close();
        }

        return true;
    }

    /**
     * 高速采集插值
     *
     * @param series
     * @param flagFinal
     */
    public void interplationXYSeries(XYSeries series, int flagFinal) {
        if (flagFinal == 0) {
            double x1 = series.getX(series.getItemCount() - 2).doubleValue();
            double y1 = series.getY(series.getItemCount() - 2).doubleValue();

            double x2 = series.getX(series.getItemCount() - 1).doubleValue();
            double y2 = series.getY(series.getItemCount() - 1).doubleValue();

            double x = (x1 + x2) / 2;
            double y = (y1 + y2) / 2;

            series.add(x, y);

        } else {
            double x1 = series.getX(series.getItemCount() - 2).doubleValue();
            double y1 = series.getY(series.getItemCount() - 2).doubleValue();

            double x2 = series.getX(series.getItemCount() - 1).doubleValue();
            double y2 = series.getY(series.getItemCount() - 1).doubleValue();

            double x = x2 + (x2 - x1) / 2;
            double y = (y1 + y2) / 2;
            series.add(x, y);
        }
    }

    public double setDivision(int fund, int restrainFrequency) {
        double dividend = -1;
        switch (fund) {
            case 1:
                fund = 1;
                if (restrainFrequency == 0) {
                    dividend = 25;
                } else {
                    dividend = 30;
                }
                return dividend;
            case 2:
                fund = 2;
                if (restrainFrequency == 0) {
                    dividend = 12.5;
                } else {
                    dividend = 15;
                }
                return dividend;
            case 3:

                fund = 4;
                if (restrainFrequency == 0) {
                    dividend = 6.25;
                } else {
                    dividend = 7.5;
                }
                return dividend;
            case 4:
                fund = 4;
                if (restrainFrequency == 0) {
                    dividend = 3.125;
                } else {
                    dividend = 3.75;
                }
                return dividend;
            case 5:
                fund = 5;
                if (restrainFrequency == 0) {
                    dividend = 250;
                }
                return dividend;
            case 6:
                fund = 6;
                if (restrainFrequency == 0) {
                    dividend = 50;
                }
                return dividend;
            case 7:
                fund = 7;
                if (restrainFrequency == 0) {
                    dividend = 100;
                }
                return dividend;
        }
        return -1;
    }

    private void changeIntegerWins(int i, XYSeries[] xyseries) {
        if (TEMSourceData.fundfrequency[i] >= 5) {//25Hz 高
            for (int m = 0; m < TEMSourceData.temData[i].length; m++) {//通道
                TEMSourceData.temData[i][m] = xyseries[m].toArray()[1];
            }
            frame.paraDialog.startTSpinner.setValue(0.001);
            if (TEMSourceData.fundfrequency[i] == 5) {
                frame.paraDialog.endTSpinner.setValue(10);
            } else if (TEMSourceData.fundfrequency[i] == 6) {
                frame.paraDialog.endTSpinner.setValue(5);
            } else if (TEMSourceData.fundfrequency[i] == 7) {
                frame.paraDialog.endTSpinner.setValue(2.5);
            }
        } else {
            if (TEMSourceData.fundfrequency[i] == 1) {
                frame.paraDialog.startTSpinner.setValue(0.033);
                frame.paraDialog.endTSpinner.setValue(10);
            } else if (TEMSourceData.fundfrequency[i] == 2) {
                frame.paraDialog.startTSpinner.setValue(0.033);
                frame.paraDialog.endTSpinner.setValue(20);
            } else if (TEMSourceData.fundfrequency[i] == 3) {
                frame.paraDialog.startTSpinner.setValue(0.033);
                frame.paraDialog.endTSpinner.setValue(40);
            } else if (TEMSourceData.fundfrequency[i] == 4) {
                frame.paraDialog.startTSpinner.setValue(0.067);
                frame.paraDialog.endTSpinner.setValue(80);
            }
        }
        frame.changeFundFre(TEMSourceData.fundfrequency[i]);//转为字符基频
    }

    /**
     * 读取文件File files数组获得数据
     *
     * @param files
     */
    private boolean readFiles(File[] files1, String path) throws FileNotFoundException, IOException {
        RandomAccessFile raf = null;
        //源数据
        int length = files1.length;
        TEMSourceData.temData = new double[length][][];
        DecimalFormat format = new DecimalFormat("0.0000000");
        int cutCounts = 0;//去掉前几个点
        for (int i = 0; i < files.length; i++) {
            raf = new RandomAccessFile(files[i], "rw");
            raf.skipBytes(8);//跳出装置形式
            TEMSourceData.Array[i] = "中心回线";
            //*******检查GPS数据有无问题*********************
            try {
                byte[] pos = new byte[128];//工作地点
                raf.read(pos);
                int recPos = 0;
                for (int j = 0; j < pos.length; j++) {
                    byte b = pos[j];
                    if (b != 0) {
                        recPos = j;
                    }
                }
                byte[] posSub = new byte[recPos + 1];//删去空格 只存地点
                for (int j = 0; j <= recPos; j++) {
                    posSub[j] = pos[j];
                }
                TEMSourceData.filesName[i] = files[i].getName();//文件名
                TEMSourceData.workPlace[i] = new String(posSub, "GBK");//工作地点

                byte[] gpsInf = new byte[32]; //经纬度
                raf.read(gpsInf);//读取gps 义
                String gpsInfStr = new String(gpsInf);
                String[] splitStrs = gpsInfStr.split(",");
                //截取纬度
                String[] latitudeSplit = splitStrs[1].split("[.]");
                String pointLat = latitudeSplit[0].substring(latitudeSplit[0].length() - 2, latitudeSplit[0].length());
                String degreeLat = latitudeSplit[0].substring(1, latitudeSplit[0].length() - 2);
                String concat = pointLat + "." + latitudeSplit[1];
                double pointLatV = Double.parseDouble(concat);
                TEMSourceData.latitude[i] = degreeLat + "°" + pointLatV + "' " + splitStrs[1].substring(0, 1);
                //经度
                String[] longitudeSplit = splitStrs[0].split("[.]");
                String pointLong = longitudeSplit[0].substring(longitudeSplit[0].length() - 2, longitudeSplit[0].length());
                String degreeLong = longitudeSplit[0].substring(1, longitudeSplit[0].length() - 2);
                String concatLong = pointLong + "." + longitudeSplit[1];
                double pointLongV = Double.parseDouble(concatLong);
                TEMSourceData.longtitude[i] = degreeLong + "°" + pointLongV + "' " + splitStrs[0].substring(0, 1);
                byte[] gpsState = new byte[2];//定位ok no
                raf.read(gpsState);//定位状态
                String gpsStateStr = new String(gpsState);
                TEMSourceData.status[i] = gpsStateStr;
                byte[] fileName = new byte[16];//文件名
                raf.read(fileName);
                byte[] gpsTime = new byte[19];//记录时间
                raf.read(gpsTime);
                String gpsTimeStr = new String(gpsTime);
                String[] gpsTimeStrs = gpsTimeStr.split(" ");
                String[] gpsTimeDateStrs = gpsTimeStrs[0].split(".");
                TEMSourceData.time[i] = gpsTimeStrs[0].concat("_").concat(gpsTimeStrs[1]);

                TEMSourceData.gain[i] = raf.readShort();//读取增益
                TEMSourceData.channels[i] = raf.read();//读取道数
                switch (TEMSourceData.channels[i]) {
                    case 1:
                        TEMSourceData.firstChannel[i] = true;
                        TEMSourceData.secondChannel[i] = null;
                        TEMSourceData.thirdChannel[i] = null;
                        break;
                    case 2:
                        TEMSourceData.firstChannel[i] = true;
                        TEMSourceData.secondChannel[i] = true;
                        TEMSourceData.thirdChannel[i] = null;
                        break;
                    case 3:
                        TEMSourceData.firstChannel[i] = true;
                        TEMSourceData.secondChannel[i] = true;
                        TEMSourceData.thirdChannel[i] = true;
                        break;
                }
                raf.skipBytes(32);
                TEMSourceData.fundfrequency[i] = raf.read();//基频号 求采样长度
//                System.out.println(TEMSourceData.fundfrequency[i]);
//                SpinnerNumberModel modelendDep = (SpinnerNumberModel) frame.paraDialog.timeWins.getModel();
//                modelendDep.setMaximum(TEMSourceData.fundfrequency[i] * 300 / 5);
                double dividend = -1;//被除数 根据基频大小 25-16 12.5-8....
                switch (TEMSourceData.fundfrequency[i]) {
                    case 1:
                        TEMSourceData.fundfrequency[i] = 1;
                        dividend = 25;
                        break;
                    case 2:
                        TEMSourceData.fundfrequency[i] = 2;
                        dividend = 12.5;
                        break;
                    case 3:
                        TEMSourceData.fundfrequency[i] = 3;
                        dividend = 6.25;
                        break;
                    case 4:
                        TEMSourceData.fundfrequency[i] = 4;
                        dividend = 3.125;
                        break;
                }
                //判定文件是不是同一频率采集的
                if (i > 0) {
                    if (TEMSourceData.fundfrequency[i] != TEMSourceData.fundfrequency[i - 1]) {
                        JOptionPane.showMessageDialog(null, "所选文件基频不全相同：" + TEMSourceData.filesName[i]);
                        return false;
                    }
                }
                TEMSourceData.superposition[i] = raf.readShort();//获得叠加次数
                raf.skipBytes(512 - (int) raf.getFilePointer());//跳出文件头
                if (dividend == -1) {
                    JOptionPane.showMessageDialog(null, files[i].getName() + "叠加次数不存在");
                }
//                TEMSourceData.temData[i] = new double[TEMSourceData.channels[i]][TEMSourceData.fundfrequency[i] * 300];//-2去掉前两个点 
                if (TEMSourceData.fundfrequency[i] <= 2) {
                    TEMSourceData.temData[i] = new double[TEMSourceData.channels[i]][TEMSourceData.fundfrequency[i] * 300 - cutCounts];//去掉前两个点 
                } else {
                    TEMSourceData.temData[i] = new double[TEMSourceData.channels[i]][1200 - cutCounts];//
                }
                //初始化其他的值默认为0D
                TEMSourceData.trWidth[i] = 1D;//Tx边长X(m)
                TEMSourceData.trLength[i] = 1D;//Tx边长Y(m)
                TEMSourceData.trCenterX[i] = 0D;
                TEMSourceData.trCenterY[i] = 0D;
                TEMSourceData.trAngle[i] = 0D;
                TEMSourceData.trTurns[i] = 1D;
                //经纬度转换
                double pointLongVv = Double.parseDouble(concatLong) / 60;
                double degreeLongV = Double.parseDouble(degreeLong);
                double pointLatVv = Double.parseDouble(concat) / 60;
                double degreeLatV = Double.parseDouble(degreeLat);
                double log = degreeLongV + pointLongVv;
                double lat = degreeLatV + pointLatVv;
//                double log = Double.parseDouble(format.format(degreeLongV + pointLongVv).toString());
//                double lat = Double.parseDouble(format.format(degreeLatV + pointLatVv).toString());
                double[] xyPos = GaussToBLToGauss.GaussToBLToGauss(log, lat);
                TEMSourceData.rCenterX[i] = Double.parseDouble(format.format(xyPos[0]));
                TEMSourceData.rCenterY[i] = Double.parseDouble(format.format(xyPos[1]));
                TEMSourceData.rCenterZ[i] = 0D;
                TEMSourceData.rArea[i] = 1D;
                TEMSourceData.turnOffTime[i] = 0D;
                TEMSourceData.current[i] = 1D;
            } catch (Exception e) {
//                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "文件：" + files[i].getName() + "GPS信息存在问题，请检查！");
                initalParameters();
                return false;
            }
            //限定积分视窗范围
            changeIntegerWins(i, null);
            //数据提取
            try {
                for (int j = 0; j < TEMSourceData.temData[i][0].length; j++) {
                    if (j == 0) {//每道跳出前两个数据 需要乘以通道数
                        raf.skipBytes(cutCounts * 4 * TEMSourceData.temData[i].length);
                    }
                    for (int m = 0; m < TEMSourceData.temData[i].length; m++) {
                        //先读取前两个数据 不要
                        //读取数据
                        try {
//                            TEMSourceData.temData[i][m][j] = raf.readInt() * 1.0 / TEMSourceData.gain[i];
                            TEMSourceData.temData[i][m][j] = 10000 * (raf.readInt() * 1.0 / TEMSourceData.gain[i] / Math.pow(2, 24));//伏特
//                            System.out.println(TEMSourceData.temData[i][m][j]);
                        } catch (Exception e) {
                            TEMSourceData.temData[i][m][j] = 0;
//                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "源文件数据量不够！显示存在问题，请检查源数据！");
                return false;
            }
            raf.close();
        }
        //删除文件夹
        deleteFile(path);
        return true;
    }

    private void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {                    //判断文件是否存在
            if (file.isFile()) {                    //判断是否是文件
                file.delete();                       //delete()方法;
            } else if (file.isDirectory()) {              //否则如果它是一个目录
                File files[] = file.listFiles();               //声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) {            //遍历目录下所有的文件
                    files[i].delete();             //把每个文件 用这个方法进行迭代
                }
                file.delete();
            }
            System.out.println("所删除的文件存在！" + '\n');
        } else {
            System.out.println("所删除的文件不存在！" + '\n');
        }
    }
}

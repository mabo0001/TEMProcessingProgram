/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handler.geopen;

import constant.geopen.ConstantPara;
import static handler.geopen.TEMIntegrationMethod.voltMax;
import static handler.geopen.TEMIntegrationMethod.voltMin;
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
import javax.swing.JFileChooser;
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
    public int flagFileType = -1;//0标示tm;1标示gptm;2标示已经积分;-1标示出现错误;3标示为ctm
    public File[] files = null;
    public static int flagGF = -1;//默认地面数据0

    public TEMData(TEMProcessingProgramWin frame) {
        this.frame = frame;//初始化界面
    }

    /**
     * 打开文件
     *
     * @param frame 主界面
     */
    public File[] openFileButtAction() {
        File[] files = null;
        //读取文件
        fileFilter = new TEMFileFilter(ConstantPara.fileAbsPath);
        customFileFilterFormat.removeFileFilter(fileFilter);
        if (TEMData.flagGF == 1) {//ctm 有别于之前老版本；包含电流数据
            customFileFilterFormat.setFileFilter(fileFilter,
                    new customFileFilterFormat(new String[]{".gptm"}, "gptm (*.gptm)"),
                    new customFileFilterFormat(new String[]{".tm"}, "tm (*.tm)"),
                    new customFileFilterFormat(new String[]{".ctm"}, "ctm (*.ctm)"));
        } else {
            customFileFilterFormat.setFileFilter(fileFilter,
                    new customFileFilterFormat(new String[]{".gptm"}, "gptm (*.gptm)"),
                    new customFileFilterFormat(new String[]{".tm"}, "tm (*.tm)"));
        }

        fileFilter.setMultiSelectionEnabled(true);//设定可以选定多个文件
        if (fileFilter.showOpenDialog(frame) == TEMFileFilter.APPROVE_OPTION) {
            if (fileFilter.getSuf().equalsIgnoreCase("tm") || fileFilter.getSuf().equalsIgnoreCase("ctm")) {
                //初始化
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
            } else if (fileFilter.getSuf().equalsIgnoreCase("ctm")) {//当读取的是usf数据
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
        frame.clearComponents();
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
        setMinMaxVolt();
        //更改积分窗口值
        for (int i = 0; i < TEMSourceData.fundfrequency.length; i++) {
            changeIntegerWins(i, null);
        }
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
        for (int i = 0; i < rowCount; i++) {
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

    public void setMinMaxVolt() {
        int length = TEMSourceData.filesName.length;
        for (int i = 0; i < length; i++) {
            ArrayList<ArrayList> Volt_midT_Current_Area_winT_Points = TEMSourceData.integrationValue.get(TEMSourceData.filesName[i]);
            double vmin = (Double) Collections.min(Volt_midT_Current_Area_winT_Points.get(0));
            double vmax = (Double) Collections.max(Volt_midT_Current_Area_winT_Points.get(0));
            if (voltMin > vmin) {
                voltMin = vmin;
            }
            if (voltMax < vmax) {
                voltMax = vmax;
            }
        }
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
        this.files = null;
        if (flagGF == 1) {//连续采集数据 飞行
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
            error = read_HighLowFile(this.files, path);
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
//        String workPos = new String(posSub, "GBK");
//        TEMAcquisitionProMain.daPosPath.setText(workPos);
        byte[] gpsInf = new byte[32];//经纬度
        raf.read(gpsInf);//读取gps 义
        try {
//            String gpsInfStr = new String(gpsInf);
//            String[] splitStrs = gpsInfStr.split("[,]");
//            //截取纬度
//            String[] latitudeSplit = splitStrs[1].split("[.]");
//            String pointLat = latitudeSplit[0].substring(latitudeSplit[0].length() - 2, latitudeSplit[0].length());
//            String degreeLat = latitudeSplit[0].substring(1, latitudeSplit[0].length() - 2);
//            String concat = pointLat + "." + latitudeSplit[1];
//            double pointLatV = Double.parseDouble(concat);
//
//            double pointLatVV = Double.parseDouble(concat) / 60.0;
//            double degreeLatVV = Double.parseDouble(degreeLat) + pointLatVV;
//            //经度
//            String[] longitudeSplit = splitStrs[0].split("[.]");
//            String pointLong = longitudeSplit[0].substring(longitudeSplit[0].length() - 2, longitudeSplit[0].length());
//            String degreeLong = longitudeSplit[0].substring(1, longitudeSplit[0].length() - 2);
//            String concatLong = pointLong + "." + longitudeSplit[1];
//            double pointLongV = Double.parseDouble(concatLong);
//            double pointLonVV = Double.parseDouble(concatLong) / 60.0;
//            double degreeLonVV = Double.parseDouble(degreeLong) + pointLonVV;
        } catch (Exception e) {
//            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "GPS信息有误！");
        }
        byte[] gpsState = new byte[2];//定位ok no
        raf.read(gpsState);//定位状态
//        String gpsStateStr = new String(gpsState);
//        TEMAcquisitionProMain.locationState.setText(gpsStateStr);
        byte[] fileName = new byte[16];//文件名
        raf.read(fileName);

        byte[] gpsTime = new byte[19];//记录时间
        raf.read(gpsTime);
        String gpsTimeStr = new String(gpsTime);

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
        byte[] channelEH = new byte[32];//通道定义 208
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
        raf.read();//基频号 求采样长度 240
        raf.readShort();//获得叠加次数 241 

        raf.readShort();//获得站点编号 243
        raf.skipBytes(7);//按键标示1 电阻6  245
        raf.read();//抑制频率 252
        raf.skipBytes(28);//253
        byte[] mode = new byte[8];
        raf.read(mode);

        raf.skipBytes(offset + 512 - (int) raf.getFilePointer());//跳出文件头
        return infor;
    }

    public String makeFileDir(File file) {
        File[] roots = File.listRoots();//获得盘符
        File dir = new File(roots[0].toString().split("[:]")[0] + ":\\" + file.getName().split("[.]")[0]);
        if (dir.exists()) {
            deleteFile(roots[0].toString().split("[:]")[0] + ":\\" + file.getName().split("[.]")[0]);
        }
        dir.mkdir();
        String path = dir.getAbsolutePath();
        return path;
    }

    /**
     * 分割文件
     */
    public String splitFiles(File[] files) throws FileNotFoundException, IOException {
        //分割文件
        byte[] buffer;
        int pos = -1;
        String suf = files[0].getName().split("[.]")[1];//区分 tm 和 ctm扩展名
        String path = makeFileDir(new File("temData"));
//        System.out.println(files.length);
        for (int i = 0; i < files.length; i++) {
//            path = makeFileDir(files[i]);//生成文件夹
            RandomAccessFile raf = new RandomAccessFile(files[i], "rw");
            int fileLength = (int) files[i].length();
            buffer = new byte[fileLength];
            FileInputStream fis = new FileInputStream(files[i]);
            DataInputStream dis = new DataInputStream(fis);
            dis.read(buffer);
            dis.close();
            int length = (int) raf.length();
//             System.out.println(files[i].getName());
            if (pos == -1) {
                for (int j = 0; j < fileLength; j += 8) {
                    byte[] b = Arrays.copyOfRange(buffer, j, j + 8);
                    String str = new String(b, "ASCII");
                    if (str.trim().equalsIgnoreCase("TEM") && j != 0) {
                        pos = j;
                        break;
                    }
                }
            }

            int remainder = length % pos;
            int N = length / pos;
            if (remainder != 0) {
                N = (int) Math.round(length * 1.0 / pos);
            }
            String station = files[i].getName().substring(0, 5);//站点名
//            System.out.println(station);
            for (int k = 0; k < N; k++) {
                int poss = k * pos;
                raf.seek(poss);
                String path2 = path + "\\" + station + extractHeadInfor(raf, poss) + "." + suf;
                File file1 = new File(path2);
                FileOutputStream fos = new FileOutputStream(file1);
                DataOutputStream dos = new DataOutputStream(fos);
                dos.write(Arrays.copyOfRange(buffer, poss, (k + 1) * pos));
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
        TEMSourceData.latitude = new String[length];//纬度
        TEMSourceData.longtitude = new String[length];//经度

        TEMSourceData.status = new String[length];;//定位状态
        TEMSourceData.time = new String[length];;//采样时间
        TEMSourceData.gain = new int[length];;//增益
        TEMSourceData.channels = new int[length];;//道数
        TEMSourceData.fundfrequency = new int[length];//基频
        TEMSourceData.superposition = new int[length];//叠加次数
        TEMSourceData.mode = new String[length];//采集模式
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
//                String gpsInfStr = new String(gpsInf).trim();
//                if (gpsInfStr.equalsIgnoreCase("")) {
////                    System.out.println(gpsInfStr.trim().equalsIgnoreCase(""));
//                    return false;
//                }
//                String[] splitStrs = gpsInfStr.split(",");
                //截取纬度
//                String[] latitudeSplit = splitStrs[1].split("[.]");
//                String pointLat = latitudeSplit[0].substring(latitudeSplit[0].length() - 2, latitudeSplit[0].length());
//                String degreeLat = latitudeSplit[0].substring(1, latitudeSplit[0].length() - 2);
//                String concat = pointLat + "." + latitudeSplit[1];
//                double pointLatV = Double.parseDouble(concat);
//                System.out.println(degreeLat + "°" + pointLatV + "' " + splitStrs[1].substring(0, 1) + "*****");
//                TEMSourceData.latitude[i] = degreeLat + "°" + pointLatV + "' " + splitStrs[1].substring(0, 1);
                //经度
//                String[] longitudeSplit = splitStrs[0].split("[.]");
//                String pointLong = longitudeSplit[0].substring(longitudeSplit[0].length() - 2, longitudeSplit[0].length());
//                String degreeLong = longitudeSplit[0].substring(1, longitudeSplit[0].length() - 2);
//                String concatLong = pointLong + "." + longitudeSplit[1];
//                double pointLongV = Double.parseDouble(concatLong);
                byte[] gpsState = new byte[2];//定位ok no
                raf.read(gpsState);//定位状态
//                String gpsStateStr = new String(gpsState);
//                TEMSourceData.status[i] = gpsStateStr;
                byte[] fileName = new byte[16];//文件名
                raf.read(fileName);
                byte[] gpsTime = new byte[19];//记录时间
                raf.read(gpsTime);

//                String gpsTimeStr = new String(gpsTime);
//                String[] gpsTimeStrs = gpsTimeStr.split(" ");
//                String[] gpsTimeDateStrs = gpsTimeStrs[0].split(".");
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
//                System.out.println(TEMSourceData.fundfrequency[i]);
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
    public boolean read_HighLowFile(File[] files1, String path) throws FileNotFoundException, IOException {
        RandomAccessFile raf = null;
//        //存储所有文件的GPS信息
        int length = files1.length;
        TEMSourceData.division = new double[length];//实际基频值
        //源数据
        TEMSourceData.temData = new double[length][][];
        DecimalFormat format = new DecimalFormat("0.0000000");
        int cutCounts = 0;//去掉前几个点
        for (int i = 0; i < files.length; i++) {
//            System.out.println(files1[i].getName());
            raf = new RandomAccessFile(files[i], "r");
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
//            raf.skipBytes(32);//跳过没有用的参数 经纬度
            byte[] gpsInf = new byte[32]; //经纬度
            raf.read(gpsInf);//读取gps 义 



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
            raf.skipBytes(27);//253
            byte[] mode = new byte[8];
            raf.read(mode);
            TEMSourceData.mode[i] = new String(mode, "ASCII");
//            System.out.println(TEMSourceData.mode[i]);
            //初始化其他的值默认为0D
            TEMSourceData.trWidth[i] = 1D;//Tx边长X(m)
            TEMSourceData.trLength[i] = 1D;//Tx边长Y(m)
            TEMSourceData.trCenterX[i] = 0D;
            TEMSourceData.trCenterY[i] = 0D;
            TEMSourceData.trAngle[i] = 0D;
            TEMSourceData.trTurns[i] = 1D;

            TEMSourceData.rCenterZ[i] = 0D;
            TEMSourceData.rArea[i] = 1D;
            TEMSourceData.turnOffTime[i] = 0D;
            TEMSourceData.current[i] = 1D;

            /*
             1、	SER     自发射采集模式:按采集键，直接开始按参数进行发射和同步采集，文件名取屏幕RTC的时间；
             2、	SER+GPS 秒脉冲同步发射采集模式：按采集键，等待一个秒脉冲开始发射和同步采集、文件名取GPS的时间；
             3、	SR+GPS秒脉冲同步采集模式：按采集键，等待一个秒脉冲开始同步采集，内部PWM不输出、文件名取GPS的时间；
             5、	CSER+GPS  秒脉冲同步连续采集发射模式：按采集键，等待一个秒脉冲开始发射和同步采集、文件名取GPS的时间，连续采集存盘，每300个文件合并存储在一个文件中；
             6、	CSR+GPS   秒脉冲同步连续采集模式：按采集键，等待一个秒脉冲开始同步采集、内部PWM不输出，文件名取GPS的时间，连续采集存盘，每300个文件合并存储在一个文件中；
             7、	OutSYS  外同步模式:按采集键，等待外部触发信号，收到后，开始同步采集，文件名再开始等待触发前取屏幕RTC的时间；
             */
//            if (TEMSourceData.mode[i].equalsIgnoreCase("CSER_GPS")
//                    || TEMSourceData.mode[i].equalsIgnoreCase("SER")
//                    || TEMSourceData.mode[i].equalsIgnoreCase("CSR_GPS")
//                    || TEMSourceData.mode[i].equalsIgnoreCase("CS_GPS")
//                    || TEMSourceData.mode[i].equalsIgnoreCase("SER_GPS")) {
            if (TEMSourceData.mode[i].toUpperCase().contains("SER")
                    || TEMSourceData.mode[i].toUpperCase().contains("CSR")
                    || TEMSourceData.mode[i].toUpperCase().contains("CS")) {
                try {
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

                    //经纬度转换
                    double pointLongVv = Double.parseDouble(concatLong) / 60;
                    double degreeLongV = Double.parseDouble(degreeLong);
                    double pointLatVv = Double.parseDouble(concat) / 60;
                    double degreeLatV = Double.parseDouble(degreeLat);
                    double log = degreeLongV + pointLongVv;
                    double lat = degreeLatV + pointLatVv;
                    double[] xyPos = GaussToBLToGauss.GaussToBLToGauss(log, lat);
                    TEMSourceData.rCenterX[i] = Double.parseDouble(format.format(xyPos[0]));
                    TEMSourceData.rCenterY[i] = Double.parseDouble(format.format(xyPos[1]));

                } catch (Exception e) {
//                    continue;
                }
//                System.out.println( TEMSourceData.rCenterX[i]);
            }

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
            int unitChannelPoints = 0;
            if (TEMSourceData.fundfrequency[i] <= 2) {
                TEMSourceData.temData[i] = new double[TEMSourceData.channels[i]][TEMSourceData.fundfrequency[i] * 300 - cutCounts];//去掉前两个点 
            } else if (TEMSourceData.fundfrequency[i] <= 4 && TEMSourceData.fundfrequency[i] > 2) {
                unitChannelPoints = 1200;
                TEMSourceData.temData[i] = new double[TEMSourceData.channels[i]][unitChannelPoints - cutCounts];
            } else if (TEMSourceData.fundfrequency[i] == 5) {//25Hz高
                unitChannelPoints = 5000;
                TEMSourceData.temData[i] = new double[TEMSourceData.channels[i]][unitChannelPoints - cutCounts];
            } else if (TEMSourceData.fundfrequency[i] == 6) {//50Hz高
                unitChannelPoints = 2500;
                TEMSourceData.temData[i] = new double[TEMSourceData.channels[i]][unitChannelPoints - cutCounts];
            } else if (TEMSourceData.fundfrequency[i] == 7) {//100Hz高
                unitChannelPoints = 1250;
                TEMSourceData.temData[i] = new double[TEMSourceData.channels[i]][unitChannelPoints - cutCounts];
            }
            //数据提取
            //保存文件
            DecimalFormat df = new DecimalFormat("0.0000");
            try {
                XYSeries[] xyseries = new XYSeries[TEMSourceData.channels[i]];
                for (int j = 0; j < TEMSourceData.channels[i]; j++) {
                    xyseries[j] = new XYSeries("时间/电压", true);
                }
                int length1 = unitChannelPoints;
                int channels = TEMSourceData.temData[i].length;
                for (int j = 0; j < length1; j++) {//采样点数
                    for (int m = 0; m < channels; m++) {//通道数
                        //读取数据
                        double value;
                        try {
                            if (TEMSourceData.fundfrequency[i] >= 5) {//高速采集
                                value = 8192 * (raf.readInt() * 1.0 / TEMSourceData.gain[i] / Math.pow(2, 18));//伏特
                                xyseries[m].add((j + 1) * 1000.0 / 500000 - 0.001, value);
                                if (j >= 0 && j < length1 - 1) {
                                    interplationXYSeries(xyseries[m], 0);
                                } else if (j == length1 - 1) {
                                    interplationXYSeries(xyseries[m], 1);
                                }
                            } else {//正常采集
                                value = 10000 * (raf.readInt() * 1.0 / TEMSourceData.gain[i] / Math.pow(2, 24));
                                TEMSourceData.temData[i][m][j] = value; //毫伏
                            }
                        } catch (Exception e) {
                            value = 0;
                            TEMSourceData.temData[i][m][j] = value;//null
                        }
                        if (i == 1 && j == 0 && m == 0) {
                            frame.yMax = value;
                            frame.yMin = value;
                        } else {
                            if (value > frame.yMax) {
                                frame.yMax = value;
                            }
                            if (value < frame.yMin) {
                                frame.yMin = value;
                            }
                        }
                    }
                }
                TEMProcessingProgramWin.yMinZ = frame.yMin;
                TEMProcessingProgramWin.yMaxZ = frame.yMax;
                //读取电流数据 0.625微妙一个数据点
                if (TEMSourceData.mode[i].equalsIgnoreCase("CSER_GPS")
                        || TEMSourceData.mode[i].equalsIgnoreCase("SER")
                        || TEMSourceData.mode[i].equalsIgnoreCase("CSR_GPS")
                        || TEMSourceData.mode[i].equalsIgnoreCase("CS_GPS")
                        || TEMSourceData.mode[i].equalsIgnoreCase("SER_GPS")) {
                    int segments = (int) (Math.ceil(unitChannelPoints * channels * 4.0 / 512));
                    raf.seek(segments * 512 + 512);
//                raf.skipBytes((int) (segments * 512 + 512-raf.getFilePointer()));
//                System.out.println(segments * 512 + 512);
//                System.out.println(segments * 512 + 512-raf.getFilePointer());
                    XYSeries currentSeries = new XYSeries("时间电流值", false, false);
                    double currentRate = 1600000;
                    double sumStart = 0;
                    double sumEnd = 0;
                    try {
                        for (int m = 0; m < 1250; m++) {
//                        double value = raf.readInt() * 3300.0 * -1 / 4096 / 75;
                            byte bb[] = new byte[4];
                            raf.read(bb);
                            double value = byteArrayToInt(bb) * 3300.0 * -1 / 4096 / 75;
//                    System.out.println(raf.getFilePointer());
                            if (m < 50) {//计算前50点的平均值
                                sumStart += value;
                            } else if (m > 1200) {//计算后50点的平均值
                                sumEnd += value;
                            }
                            currentSeries.add((m + 1) * 1000.0 * 1000 / currentRate, value);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                    double avCurrentS = sumStart / 50;
                    double avCurrentE = sumEnd / 50;
                    TEMSourceData.current[i] = Double.parseDouble(df.format(Math.abs(avCurrentS - avCurrentE)));
//                    TEMSourceData.current[i] = Math.abs(avCurrentS - avCurrentE);
//                System.out.println(segments + "电压扇区；" + "avCurrentS=" + avCurrentS + " avCurrentE=" + avCurrentE);
                }
                //更改积分时窗范围
                changeIntegerWins(i, xyseries);
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
        //删除文件夹
        deleteFile(path);
        return true;
    }

    /**
     * byte数组转换为int整数
     *
     * @param bytes byte数组
     * @param off 开始位置
     * @return int整数
     */
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF
                | (b[2] & 0xFF) << 8
                | (b[1] & 0xFF) << 16
                | (b[0] & 0xFF) << 24;
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

    /**
     * 读取文件时更改时间窗口
     *
     * @param i
     * @param xyseries
     */
    private void changeIntegerWins(int i, XYSeries[] xyseries) {
        if (TEMSourceData.fundfrequency[i] >= 5) {//25Hz 高
            if (xyseries != null) {//只针对处gptm文件以外的采集数据
                for (int m = 0; m < TEMSourceData.temData[i].length; m++) {//通道
                    TEMSourceData.temData[i][m] = xyseries[m].toArray()[1];
                }
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
            int unitChannelPoints = 0;
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
//                System.out.println(gpsInfStr);
                String[] splitStrs = gpsInfStr.split(",");
                String concatLong = "";
                String degreeLong = "";
                try {
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
                    degreeLong = longitudeSplit[0].substring(1, longitudeSplit[0].length() - 2);
                    concatLong = pointLong + "." + longitudeSplit[1];
                    double pointLongV = Double.parseDouble(concatLong);
                    TEMSourceData.longtitude[i] = degreeLong + "°" + pointLongV + "' " + splitStrs[0].substring(0, 1);
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

                } catch (Exception e) {
                    TEMSourceData.latitude[i] = "Error";
                    TEMSourceData.longtitude[i] = "Error";
                    TEMSourceData.status[i] = "NO";
                    TEMSourceData.rCenterX[i] = 0;
                    TEMSourceData.rCenterY[i] = 0;
                    TEMSourceData.rCenterZ[i] = 0D;
                    raf.seek(8 + 128 + 32);
                }

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
                raf.skipBytes(32);//跳出通道定义
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
                raf.skipBytes(37);//跳出叠加次数之后的字节直接到 mode位置
                byte[] mode = new byte[8];//
                raf.read(mode);
                TEMSourceData.mode[i] = new String(mode, "ASCII");
//                System.out.println(TEMSourceData.mode[i]);
                raf.skipBytes(512 - (int) raf.getFilePointer());//跳出文件头
                if (dividend == -1) {
                    JOptionPane.showMessageDialog(null, files[i].getName() + "叠加次数不存在");
                }
//                TEMSourceData.temData[i] = new double[TEMSourceData.channels[i]][TEMSourceData.fundfrequency[i] * 300];//-2去掉前两个点 
//                System.out.println(TEMSourceData.fundfrequency[i]);
                if (TEMSourceData.fundfrequency[i] <= 2) {
                    unitChannelPoints = TEMSourceData.fundfrequency[i] * 300;
                    TEMSourceData.temData[i] = new double[TEMSourceData.channels[i]][unitChannelPoints - cutCounts];//去掉前两个点 
                } else {
                    unitChannelPoints = 1200;
                    TEMSourceData.temData[i] = new double[TEMSourceData.channels[i]][1200 - cutCounts];//
                }
                //初始化其他的值默认为0D
                TEMSourceData.trWidth[i] = 1D;//Tx边长X(m)
                TEMSourceData.trLength[i] = 1D;//Tx边长Y(m)
                TEMSourceData.trCenterX[i] = 0D;
                TEMSourceData.trCenterY[i] = 0D;
                TEMSourceData.trAngle[i] = 0D;
                TEMSourceData.trTurns[i] = 1D;
                TEMSourceData.rArea[i] = 1D;
                TEMSourceData.turnOffTime[i] = 0D;
                TEMSourceData.current[i] = 1D;
            } catch (Exception e) {
                e.printStackTrace();
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
                        double value = 10000.0 * (raf.readInt() * 1.0 / TEMSourceData.gain[i] / Math.pow(2, 24));//伏特
                        try {
                            TEMSourceData.temData[i][m][j] = value;
                        } catch (Exception e) {
                            value = 0;
                            TEMSourceData.temData[i][m][j] = 0;
                        }
                        if (i == 1 && j == 0 && m == 0) {
                            frame.yMax = value;
                            frame.yMin = value;
                        } else {
                            if (value > frame.yMax) {
                                frame.yMax = value;
                            }
                            if (value < frame.yMin) {
                                frame.yMin = value;
                            }
                        }
                    }
                }
                TEMProcessingProgramWin.yMinZ = frame.yMin;
                TEMProcessingProgramWin.yMaxZ = frame.yMax;
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "源文件数据量不够！显示存在问题，请检查源数据！");
                return false;
            }
            //读取电流数据 0.625微妙一个数据点
            DecimalFormat df = new DecimalFormat("0.0000");
            if (TEMSourceData.mode[i].equalsIgnoreCase("CSER_GPS")
                    || TEMSourceData.mode[i].equalsIgnoreCase("SER")
                    || TEMSourceData.mode[i].equalsIgnoreCase("CSR_GPS")
                    || TEMSourceData.mode[i].equalsIgnoreCase("CS_GPS")
                    || TEMSourceData.mode[i].equalsIgnoreCase("SER_GPS")) {
                int segments = (int) (Math.ceil(unitChannelPoints * TEMSourceData.channels[i] * 4.0 / 512));
                raf.seek(segments * 512 + 512);
                XYSeries currentSeries = new XYSeries("时间电流值", false, false);
                double currentRate = 1600000;
                double sumStart = 0;
                double sumEnd = 0;
                try {
                    for (int m = 0; m < 1250; m++) {
//                        double value = raf.readInt() * 3300.0 * -1 / 4096 / 75;
                        byte bb[] = new byte[4];
                        raf.read(bb);
                        double value = byteArrayToInt(bb) * 3300.0 * -1 / 4096 / 75;
//                    System.out.println(raf.getFilePointer());
                        if (m < 50) {//计算前50点的平均值
                            sumStart += value;
                        } else if (m > 1200) {//计算后50点的平均值
                            sumEnd += value;
                        }
                        currentSeries.add((m + 1) * 1000.0 * 1000 / currentRate, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
                double avCurrentS = sumStart / 50;
                double avCurrentE = sumEnd / 50;
//                TEMSourceData.current[i] = Math.abs(avCurrentS - avCurrentE);
                TEMSourceData.current[i] = Double.parseDouble(df.format(Math.abs(avCurrentS - avCurrentE)));
//                System.out.println(segments + "电压扇区；" + "avCurrentS=" + avCurrentS + " avCurrentE=" + avCurrentE);
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

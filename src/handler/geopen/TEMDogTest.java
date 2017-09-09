/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handler.geopen;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;
//import temprogramdog.*;

public class TEMDogTest {

    public static void startRun() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                //检测狗的存在
//                TEMProgramDog dog = new TEMProgramDog();
//                int recP = dog.protecting();
//                if (recP == 3) {
//                    JOptionPane.showMessageDialog(null, "读取加密狗出现错误，请插入与TEM程序匹配的加密狗或退出不匹配的加密狗！", "加密狗检测", JOptionPane.ERROR_MESSAGE);
//                    System.exit(0);
//                    return;
//                } else if (recP == 0) {
//                    JOptionPane.showMessageDialog(null, "没有检测到加密狗，请插入与TEM程序匹配的加密狗！", "加密狗检测", JOptionPane.ERROR_MESSAGE);
//                    System.exit(0);
//                    return;
//                }
            }
        };
        timer.scheduleAtFixedRate(task, new Date(), 5000);//当前时间开始起动 每次间隔5秒再启动
//        timer.scheduleAtFixedRate(task, 1000, 2000); // 1秒后启动  每次间隔2秒再启动
    }
}
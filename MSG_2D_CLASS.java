package ee382n;

import java.util.ArrayList;

public class MSG_2D_CLASS {
    int pid;
    int round;
    ArrayList<Double> msg2d = new ArrayList<Double>();

    public MSG_2D_CLASS(int pid, int round, ArrayList msg2d) {
        this.pid = pid;
        this.round=round;
        this.msg2d = msg2d;
    }

    public int getPid() {
        return pid;
    }
    public String toMessageString() {
        String messageString = "";
        for(double d: msg2d) messageString=messageString+Double.toString(d)+" ";
        return Integer.toString(pid)+" "+Integer.toString(round)+" "+ messageString;
    }

    public void fromMessageValue(String messageString){
        //"1 1 2.2 3.3 "
        String [] messageStringArray = messageString.split(" ");

        this.pid = Integer.parseInt(messageStringArray[0]);
        this.round = Integer.parseInt(messageStringArray[1]);

        ArrayList<Double> tempList = null;
        for(int i = 2; i<messageStringArray.length; i++) tempList.add(Double.parseDouble(messageStringArray[i]));

        this.msg2d = tempList;

    }

    public int getRound() {
        return round;
    }

    public ArrayList<Double> getMsg2d() {
        return msg2d;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public void setPid(int pid) {
        this.pid = pid;

    }

    public void setMsg2d(ArrayList<Double> msg2d) {
        this.msg2d = msg2d;
    }
}

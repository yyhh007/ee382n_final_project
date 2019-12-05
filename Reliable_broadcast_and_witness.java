package ee382n;

import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Reliable_broadcast_and_witness {
    //private int[] serverList;
    Scanner din;
    PrintStream pout;
    Socket server;
    static int [] serverList = new int[1];
    static final int DIM=2;
    static final double EPSILON=0.1;


    //alg1
    public void RBSend(String msg, String messageType) throws IOException {

        for (int server_port: serverList) {
            System.out.println("sending message: "+msg);
            TCPSendClientRequest("localhost", server_port,  messageType+msg);
        }
    }


    public  void getSocket(String hostAddress, int port) throws IOException{
        server = new Socket(hostAddress, port);
        din = new Scanner(server.getInputStream());
        pout = new PrintStream(server.getOutputStream());
    }

    public  void TCPSendClientRequest(String hostAddress, int tcpPort, String outMessage) throws IOException {
        getSocket(hostAddress, tcpPort);
        pout.println(outMessage);
        pout.flush();
    }


  public R_and_v calculateRounds(MSG_2D_CLASS msgVector){

      RBSend(msgVector, "value:");
    ArrayList<MSG_2D_CLASS> val = new ArrayList<MSG_2D_CLASS>();    //value
    ArrayList<MSG_3D_CLASS> wit = new ArrayList<MSG_3D_CLASS>();    //witness

    for (int i = 1; i<5 ;i++){
      ArrayList<Double> msgVector_temp = new ArrayList<Double>();
      msgVector_temp.add(100.0);
      msgVector_temp.add(200.0);
      MSG_2D_CLASS temp = new MSG_2D_CLASS(i, 0, msgVector_temp);
      val.add(temp);

    }

    for (int j = 1; j<5 ;j++){
        ArrayList<MSG_2D_CLASS> val_temp = new ArrayList<MSG_2D_CLASS>();    //value

        for (int i = 1; i<5 ;i++){
            ArrayList<Double> msgVector_temp = new ArrayList<Double>();
            msgVector_temp.add(2.5);
            msgVector_temp.add(3.5);
            MSG_2D_CLASS temp = new MSG_2D_CLASS(i, 0, val_temp);
            val_temp.add(temp);


        }
        MSG_3D_CLASS temp_3d = new MSG_3D_CLASS(j,0,val_temp);
        wit.add(temp_3d);


    }

      ArrayList<Point2D> barycenter_array = new ArrayList<>();
    for(MSG_3D_CLASS w_subset: wit){
        ArrayList<MSG_2D_CLASS> w_sub_2d = w_subset.getMsg3d();
        Point2D[] w_sub_corners = new Point2D[w_sub_2d.size()];
        ConvexPolygon2D w_prime_safe_area=null;
        Point2D berycenter_output;
        int i2d =0;
        for(MSG_2D_CLASS sub_2d: w_sub_2d){
            w_sub_corners[i2d].setX(sub_2d.getMsg2d().get(0));
            w_sub_corners[i2d].setY(sub_2d.getMsg2d().get(1));
            i2d++;
        }
        ArrayList<Point2D> safe_input_w_sub_corners = new ArrayList<Point2D>(Arrays.asList(w_sub_corners));
        w_prime_safe_area = w_prime_safe_area.GetSafeArea(w_prime_safe_area.OrderClockwise(safe_input_w_sub_corners));
        berycenter_output = w_prime_safe_area.computeCentroid(w_prime_safe_area);
        barycenter_array.add(berycenter_output);
    }


    ConvexPolygon2D u_safe_area=null;
    u_safe_area = u_safe_area.GetSafeArea(u_safe_area.OrderClockwise(barycenter_array));
    //line 4
    Point2D output_v;
    output_v = u_safe_area.computeCentroid(u_safe_area);
    //line 5
    double test_range = u_safe_area.GetMaxRange(u_safe_area);
    int R = (int) Math.ceil(Math.log(Math.sqrt(DIM)*u_safe_area.GetMaxRange(u_safe_area)/EPSILON)/Math.log(2));

    R_and_v output_r_and_v = new R_and_v(R, output_v);
      return output_r_and_v;
  }




  //algorithm 6!
  public Point2D AsyncAgreeMH(){
    ArrayList<MSG_2D_CLASS> hardCodedVal = new ArrayList<MSG_2D_CLASS>();    //value

    //fake variables for testing for now
    int fault= 1;
    int pid = 1;
    Reliable_broadcast_and_witness rbw= new Reliable_broadcast_and_witness();


    ArrayList<Double> msgVector = new ArrayList<Double>();
    msgVector.add(2.5);
    msgVector.add(3.5);

    //calculate rounds needs to be a sychronous function
    //(R, v) <- CalculateRounds(I)
    R_and_v roundandvalue = calculateRounds(msgVector);
    int ROUND = roundandvalue.getR();
    Point2D output_v = roundandvalue.getInit_v();
    //TODO convert initv to double array or change msg2dclass to take point2d parameter


    //for m -> 1, . . . , d do
    for (int m = 1; m<=DIM; m++){

      //H<-null
      ArrayList<MSG_2D_CLASS> haltMessages = new ArrayList<MSG_2D_CLASS>();

      //r <-1
      int currentRound = 1;


      while(haltMessages.size()<= fault){
          MSG_2D_CLASS newMessage= new MSG_2D_CLASS(pid, currentRound, output_v.toString());

          //rbsend(p, r, v)
          ArrayList<MSG_2D_CLASS> V = rbw.RBSend(newMessage.toMessageString(), "value:");
          //Get safe area sample code:
          Point2D[] V_corners = new Point2D[V.size()];
          ConvexPolygon2D V_safe_area=null;
          Point2D v_output;
          int i2d =0;
          for(MSG_2D_CLASS V_2d: V){
              V_corners[i2d].setX(V_2d.getMsg2d().get(0));
              V_corners[i2d].setY(V_2d.getMsg2d().get(1));
              i2d++;
          }
          ArrayList<Point2D> safe_input_V_corners = new ArrayList<Point2D>(Arrays.asList(V_corners));
          V_safe_area = V_safe_area.GetSafeArea(V_safe_area.OrderClockwise(safe_input_V_corners));

          //Update output vector line 9
          //Point2D ori_output_v = new Point2D(111,111);
          V_safe_area.update_output_mid_safe(V_safe_area,output_v);

          ArrayList<Double> temp_halt_msg = new ArrayList<Double>();
          temp_halt_msg.add(output_v.getX());
          temp_halt_msg.add(output_v.getY());
          if (currentRound == ROUND){
            MSG_2D_CLASS haltMessage= new MSG_2D_CLASS(pid, currentRound, temp_halt_msg);
            rbw.RBSend(haltMessage.toMessageString(), "halt:");
          }
          currentRound++;

      }
    }

    //return outputv here
    return output_v;
  }

  public ArrayList<MSG_2D_CLASS> RBEchoandReceiveWitness(){
        Reliable_broadcast_and_witness rbw= new Reliable_broadcast_and_witness();

        int portNumber = 5003;
        int pid = 1;
        int round = 1;
        int numServer = 1;
        boolean neverSentEcho = false;
        boolean neverSentReady = false;
        boolean recieveAllVal = false;
        int fault = 0;
        //serverList =new int[numServer];
        serverList[0] = 5002;
        ArrayList<Double> msgVector = new ArrayList<Double>();
        msgVector.add(2.5);
        msgVector.add(3.5);
        //rbw.RBSend(newMessage, "value:");
                System.out.println("tcp server on port "+Integer.toString(portNumber)+" started:");
        ServerSocket listener  = new ServerSocket(portNumber);
        MSG_2D_CLASS newMessage = new MSG_2D_CLASS(pid,round, msgVector);

        Map<String, Integer> send_echoCounter = new HashMap<>();
        Map<String, Integer> send_readyCounter = new HashMap<>();
        Map<String, Integer> receive_echoCounter = new HashMap<>();
        Map<String, Integer> receive_readyCounter = new HashMap<>();


        //algo 4
        ArrayList<MSG_2D_CLASS> val = new ArrayList<MSG_2D_CLASS>();    //value
        ArrayList<MSG_3D_CLASS> rep = new ArrayList<MSG_3D_CLASS>();    //report
        ArrayList<MSG_2D_CLASS> wit = new ArrayList<MSG_2D_CLASS>();    //witness

        ArrayList<MSG_2D_CLASS> haltMessages = new ArrayList<MSG_2D_CLASS>();

        while (true) {
            Socket connectionSocket = listener.accept();
            BufferedReader inFromClient =  new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            String message = inFromClient.readLine();
            System.out.println("Received: " + message);
            outToClient.writeBytes(message);
            MSG_2D_CLASS messageValue = null;


            String key;
            switch (message.split(":")[0]){
                //(1,1,.)
                //(1,2,.)
                //array
                //Map<String, Integer> = new HashMap<>();
                //Map<"pid.r", # of echos>
                //TODO change newmessage in these switch statements
                case "value":
                    messageValue.fromMessageValue( message.split(":")[1]);
                    key = Integer.toString(messageValue.getPid())+"."+Integer.toString(messageValue.getRound());
                    if(!send_echoCounter.containsKey(key)){
                        rbw.RBSend(newMessage.toMessageString(), "echo:");
                        //e.g. ("1.1",1)
                        send_echoCounter.put(key, 1);
                        receive_echoCounter.put(key,1);

                    }
                    break;
                    //get val for algo 4
                case "Val":
                    String messageVal=( message.split(":")[1]);
                    String []messageValString = messageVal.split(" ");

                    String [] valNum = messageValString[2].split("/");

                    ArrayList<MSG_2D_CLASS> eachValue = new ArrayList<MSG_2D_CLASS>();
                    for(int i = 0; i<valNum.length; i++){
                        MSG_2D_CLASS value = null;
                        value.fromMessageValue(valNum[i]);

                        eachValue.add(value);
                    }
                    MSG_3D_CLASS report = new MSG_3D_CLASS(Integer.parseInt(messageValString[0]), Integer.parseInt(messageValString[1]), eachValue);
                    rep.add(report);

                    //add this val array to report, then add common with current val into wit
                    if (wit.size()>= numServer-fault){
                        //done, val contains consensus
                        //return val;
                    }
                    else{
                        for (int i = 0; i<rep.size(); i++){
                            if(val.contains(rep.get(i).getMsg3d())){
                                wit=rep.get(i).getMsg3d();
                            }
                        }

                    }
                    break;
                case "echo":
                    messageValue.fromMessageValue( message.split(":")[1]);
                    //recieved echo from server 2
                    //echocoounter ("1.1",1)
                    key = Integer.toString(messageValue.getPid())+"."+Integer.toString(messageValue.getRound());
                    if(receive_echoCounter.get(key)!=null)
                        receive_echoCounter.put(key, receive_echoCounter.get(key)+1);
                    else
                        receive_echoCounter.put(key,1);

                    if(receive_echoCounter.get(key)>=(numServer-fault)){
                        if(!send_readyCounter.containsKey(key)){
                            rbw.RBSend(newMessage.toMessageString(), "ready:");
                            send_readyCounter.put(key,1);
                            receive_readyCounter.put(key,1);
                        }
                    }
                    break;
                case "ready":
                    messageValue.fromMessageValue( message.split(":")[1]);
                    val.add(messageValue);

                    key = Integer.toString(messageValue.getPid())+"."+Integer.toString(messageValue.getRound());

                    //algorithm 3 check:do we have n-f messages

                    if(receive_readyCounter.get(key)!=null)
                        receive_readyCounter.put(key, receive_readyCounter.get(key)+1);
                    else
                        receive_readyCounter.put(key,1);

                    //TODO find out whats going on next time
                    //TODO find out if we need to send and receive readycounter in the following block
                    if(receive_readyCounter.get(key)>= (fault+1)){
                        if(!send_readyCounter.containsKey(key)){
                            rbw.RBSend(newMessage.toMessageString(), "ready:");
                            send_readyCounter.put(key,1);
                            receive_readyCounter.put(key,1);
                        }
                    }

                    //if recieved n-f ready message, send out our recieved val array to all
                    if(val.size()>=(numServer-fault) &&  recieveAllVal==false){
                        recieveAllVal=true;
                        String valMessage = null;

                        for (int i = 0; i <val.size(); i++){
                            valMessage= valMessage+ val.get(i).toMessageString()+'/';
                        }

                        valMessage = Integer.toString(pid)+" "+ Integer.toString(round)+" "+valMessage;

                        //exp: 1 2 1 2 1.1 2.2/2 2 1.1 2.2
                        rbw.RBSend(valMessage.substring(0, valMessage.length() - 1), "val:");
                    }


                    break;
                case "  halt":
                  messageValue.fromMessageValue( message.split(":")[1]);
                  if(messageValue.getPid()>=currentRound){
                    haltMessage.add(messageValue);

                  }
                  break;
            }


        }
  }


    public static void main(String[] args) throws IOException {


      //create thread that running RBreceiving forever checking input channels.
        // if received halt msg add it to H
    //Call alg 6


    }
}

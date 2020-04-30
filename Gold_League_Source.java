

// Navid Bamdad Roshan


import java.util.*;
import java.io.*;
import java.math.*;

/**
 * This code automatically collects game data in an infinite loop.
 * It uses the standard input to place data into the game variables such as x and y.
 * YOU DO NOT NEED TO MODIFY THE INITIALIZATION OF THE GAME VARIABLES.
 **/
 
class Pod{
    int x;
    int y;
    int vx;
    int vy;
    int angle;
    int nextCheckPointId;
    int nextCheckpointX;
    int nextCheckpointY;

    
    public Pod(){
        x = 0;
        y = 0;
        vx = 0;
        vy = 0;
        angle = 0;
        nextCheckPointId = 0;
        nextCheckpointX = 0;
        nextCheckpointY = 0;
    }
    
    // Calculate the distance of pod and checkpoint
    public int getCheckpointDistance(){
        return((int)Math.sqrt((Math.pow(nextCheckpointX-x, 2) + Math.pow(nextCheckpointY-y, 2))));
    }
    
    // Set values to nextCheckpointX and nextCheckpointY variables
    public void setCheckpointXY(int x, int y){
        nextCheckpointX = x;
        nextCheckpointY = y;
    }
}

class OpPod extends Pod{
    int currentLap;
    private boolean lapUpdated;
    int currentOpponentDistanceFromPod1;
    int previousOpponentDistanceFromPod1;
    int currentOpponentDistanceFromPod2;
    int previousOpponentDistanceFromPod2;
    
    
    public OpPod(){
        super();
        currentLap = 1;
        lapUpdated = false;
        currentOpponentDistanceFromPod1 = 0;
        previousOpponentDistanceFromPod1 = 0;
        currentOpponentDistanceFromPod2 = 0;
        previousOpponentDistanceFromPod2 = 0;
    }
    
    public void setNextCheckpointId(int id){
        this.nextCheckPointId = id;
        
        // tracking the lap number of pod
        if (id == 1)
            lapUpdated = false;
        if (id == 0 && !lapUpdated){
            lapUpdated = true;
            currentLap++;
        }
    }
    
    // Check if this pod is beating the input pod
    public boolean isBeating(OpPod pod){
        if (this.currentLap > pod.currentLap){
            return(true);
        }else if (pod.currentLap > this.currentLap){
            return(false);
        }else if(this.nextCheckPointId > pod.nextCheckPointId){
            return(true);
        }else if(this.nextCheckPointId < pod.nextCheckPointId){
            return(false);
        }else if(this.getCheckpointDistance() < pod.getCheckpointDistance()){
            return(true);
        }else{
            return(false);
        }

    }
    
}

class MyPod extends Pod{
    int speed;
    int goToX;
    int goToY;
    int thrust;
    String strThrust;
    int angleWithNextCheckpoint;
    int angleWithLaterCheckpoint;
    public MyPod(){
        super();
        speed = 0;
        goToX = 0;
        goToY = 0;
        thrust = 100;
        strThrust = "100";
        angleWithNextCheckpoint = 0;
        angleWithLaterCheckpoint = 0;
    }
    
    // Calculate the pod speed
    public void speedCalculate(){
        speed = (int)Math.sqrt((Math.pow(vx, 2) + Math.pow(vy, 2)));
    }
    
    // Set values to goToX and goToY variables
    public void setGoTo(int x, int y){
        goToX = x;
        goToY = y;
    }
    
    // Returns if this pod is going to collide with input pod
    public boolean isGoingToCollideWith(OpPod p){
        int thisNextX = this.x+this.vx;
        int thisNextY = this.y+this.vy;
        int inputNextX = p.x+p.vx;
        int inputNextY = p.y+p.vy;
        int nextDistance = (int)Math.sqrt((Math.pow(thisNextX - inputNextX , 2) + Math.pow(thisNextY - inputNextY, 2)));
        if (nextDistance < 850)
            return(true);
        else
            return(false);
    }
}
 
 
class Player {
    
    // Return the vector from input2 to input1
    private static int[] directionVector(int x1, int y1, int x2, int y2){
        return (new int[] {x1 - x2, y1 - y2});
    }
    
    // Calculate the angle between two vectors
    private static double vectorAngle(int[] v1, int[] v2){
        return ((double)((Math.atan2(v2[1],v2[0]) - Math.atan2(v1[1],v1[0]))));
    }
    
    // Correcting the destinaltion
    private static int[] newDestination(int desVectorX, int desVectorY, int x, int y, double angle){
        
        int newDesVectorX = (int)(desVectorX * Math.cos(angle) - desVectorY * Math.sin(angle));
        int newDesVectorY = (int)(desVectorX * Math.sin(angle) + desVectorY * Math.cos(angle));
        return(new int[] {x+newDesVectorX, y+newDesVectorY});
    }
    
    // Calculate the angle between pod and checkpoint
    private static int calculatePodCheckpointAngle(int podX, int podY, int cpX, int cpY, int podAngle){
        int tempAngle = (int)(Math.toDegrees(vectorAngle(new int[]{1, 0}, directionVector(cpX, cpY, podX, podY))));  
        if (tempAngle < 0){
            tempAngle += 360;
        }
        tempAngle = Math.abs(podAngle - tempAngle);
        if (tempAngle > 180)
             tempAngle = 360 - tempAngle;
        return(tempAngle);   
    }
    
    // Keeping the range of angle in (-180 , 180)
    private static int degreeCorrection(int degree){
        if (degree > 180)
            degree = 360 - degree;
        return (degree);
    }
    
    // Keeping the range of angle in (-3.1419 , 3.1419)
    private static double angleCorrection(double angle){
        if (angle > 3.1419)
            angle = -6.28319 + angle;
        if (angle < -3.1419)
            angle = angle + 6.28319;
        return (angle);
    }
    
    
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        
        
        
        boolean boostUsed = false;
        
        // Variables
        int turn = 0;
        int lap = 1;
        
        boolean aimToLaterCheckpointIsActive = false;
        
        
        // Pod 1 state:  
        //              0: searching for new block point
        //              1: going toward block point
        //              2: reached blockpoint and waitingfor opponenet
        //              3: going to hit opponent
        int pod1State = 0;
        
        OpPod succeedingOpponentPod;
        
        // Initialization input
        int laps = in.nextInt();
        int checkpointCount = in.nextInt();
        int[] checkpointX = new int[20];
        int[] checkpointY = new int[20];
        for (int i=0; i<checkpointCount; i++){
            checkpointX[i] = in.nextInt();
            checkpointY[i] = in.nextInt();
        }
        
        MyPod pod1 = new MyPod();
        MyPod pod2 = new MyPod();
        OpPod op1 = new OpPod();
        OpPod op2 = new OpPod();
        
        // game loop
        while (true) {
            
            pod1.x = in.nextInt();
            pod1.y = in.nextInt(); 
            pod1.vx = in.nextInt();
            pod1.vy = in.nextInt();
            pod1.angle = in.nextInt();
            in.nextInt();
            // I do not need this value
            //pod1.nextCheckPointId = in.nextInt();
            
            pod2.x = in.nextInt(); 
            pod2.y = in.nextInt(); 
            pod2.vx = in.nextInt();
            pod2.vy = in.nextInt();
            pod2.angle = in.nextInt();
            pod2.nextCheckPointId = in.nextInt();
            
            op1.x = in.nextInt(); 
            op1.y = in.nextInt();
            op1.vx = in.nextInt();
            op1.vy = in.nextInt();
            op1.angle = in.nextInt();
            op1.setNextCheckpointId(in.nextInt());
            
            op2.x = in.nextInt(); 
            op2.y = in.nextInt(); 
            op2.vx = in.nextInt();
            op2.vy = in.nextInt();
            op2.angle = in.nextInt();
            op2.setNextCheckpointId(in.nextInt());
            
            // Setting next checkpoint
            pod2.setGoTo(checkpointX[pod2.nextCheckPointId], checkpointY[pod2.nextCheckPointId]);
            pod2.setCheckpointXY(checkpointX[pod2.nextCheckPointId], checkpointY[pod2.nextCheckPointId]);
            op1.setCheckpointXY(checkpointX[op1.nextCheckPointId], checkpointY[op1.nextCheckPointId]);
            op2.setCheckpointXY(checkpointX[op2.nextCheckPointId], checkpointY[op2.nextCheckPointId]);
            
            
            pod1.thrust = 100;
            pod1.strThrust = "100";
            pod2.thrust = 100;
            pod2.strThrust = "100";
            
            
            // finding later checkpoint id
            int laterCheckpointId = pod2.nextCheckPointId + 1;
            if (laterCheckpointId >= checkpointCount)
                laterCheckpointId = 0;
            int laterCheckpointX = checkpointX[laterCheckpointId];
            int laterCheckpointY = checkpointY[laterCheckpointId];
            
            //pod1.angleWithNextCheckpoint = calculatePodCheckpointAngle(pod1.x, pod1.y, pod1.nextCheckpointX, pod1.nextCheckpointY, pod1.angle);
            pod2.angleWithNextCheckpoint = calculatePodCheckpointAngle(pod2.x, pod2.y, pod2.nextCheckpointX, pod2.nextCheckpointY, pod2.angle);
            pod2.angleWithLaterCheckpoint = calculatePodCheckpointAngle(pod2.x, pod2.y, laterCheckpointX, laterCheckpointY, pod2.angle);
                        
            
            
            
            
            // Modifying the destination of pod2 to be closer to later checkpoint by 50 units
            int[] laterToNextVector = directionVector(pod2.nextCheckpointX, pod2.nextCheckpointY, laterCheckpointX, laterCheckpointY);
            double length = Math.sqrt(Math.pow(laterToNextVector[0],2) + Math.pow(laterToNextVector[1],2));
            laterToNextVector[0] = (int)((laterToNextVector[0]/length)*(length-50));
            laterToNextVector[1] = (int)((laterToNextVector[1]/length)*(length-50));
            pod2.setGoTo(laterToNextVector[0] + laterCheckpointX, laterToNextVector[1] + laterCheckpointY);
            
            
            
            
            
            
            // Calculating current speed
            pod1.speedCalculate();
            pod2.speedCalculate();
            
            // Calculating current opponent distances from my pods
            op1.currentOpponentDistanceFromPod1 = (int)Math.sqrt((Math.pow(pod1.x - op1.x, 2) + Math.pow(pod1.y - op1.y, 2)));
            op2.currentOpponentDistanceFromPod1 = (int)Math.sqrt((Math.pow(pod1.x - op2.x, 2) + Math.pow(pod1.y - op2.y, 2)));
            op1.currentOpponentDistanceFromPod2 = (int)Math.sqrt((Math.pow(pod2.x - op1.x, 2) + Math.pow(pod2.y - op1.y, 2)));
            op2.currentOpponentDistanceFromPod2 = (int)Math.sqrt((Math.pow(pod2.x - op2.x, 2) + Math.pow(pod2.y - op2.y, 2)));
            
            
            // Calculating the angle of vector from pod to next checkpoint with movement vector
            int[] destinationVector = directionVector(pod2.nextCheckpointX,pod2.nextCheckpointY,pod2.x,pod2.y);
            double pathDeviationAngle = vectorAngle(new int[]{pod2.vx,pod2.vy}, destinationVector);
            int pathDeviationDegree = Math.abs((int)Math.toDegrees(pathDeviationAngle));
            pathDeviationDegree = degreeCorrection(pathDeviationDegree);
            pathDeviationAngle = angleCorrection(pathDeviationAngle);
            
            
            
            // Thrust adjustment for pod2
            if (pod2.getCheckpointDistance() > 4000){ 
                if (pod2.angleWithNextCheckpoint  > 60)
                    pod2.thrust = (int)((100 * ((120 - pod2.angleWithNextCheckpoint ))) / 60);
                if (pod2.angleWithNextCheckpoint  > 120)
                    pod2.thrust = 0;
            }else if (pod2.getCheckpointDistance() > 3000){    
                if (pod2.angleWithNextCheckpoint  > 90)
                    pod2.thrust = 50;
                if (pod2.angleWithNextCheckpoint  > 120)
                    pod2.thrust = 0;
            }else if (pod2.getCheckpointDistance() > 2000){
                if (pod2.angleWithNextCheckpoint  > 60)
                    pod2.thrust = 60;
                if (pod2.angleWithNextCheckpoint  > 90)
                    pod2.thrust = 40;
                if (pod2.angleWithNextCheckpoint  > 120)
                    pod2.thrust = 0;
            }else if (pod2.getCheckpointDistance() > 1500){
                if (pod2.angleWithNextCheckpoint  > 30)
                    pod2.thrust = 50;
                if (pod2.angleWithNextCheckpoint  > 45)
                    pod2.thrust = 20;
                if (pod2.angleWithNextCheckpoint  > 90)
                    pod2.thrust = 0;
            }else if (pod2.getCheckpointDistance() < 1500){
                if (pod2.speed > 300)
                    pod2.thrust = 40;
                if (pod2.speed > 400)
                    pod2.thrust = 20;
                if (pod2.angleWithNextCheckpoint  > 30)
                    pod2.thrust = 20;
                if (pod2.angleWithNextCheckpoint  > 45)
                    pod2.thrust = 5;
                if (pod2.angleWithNextCheckpoint  > 90)
                    pod2.thrust = 0;
            }
            
            
            // Calculating the angle of vector from pod to next chechpoint with vector from next checkpoint to later checkpoint
            int[] nextCheckpointVector = directionVector(pod2.nextCheckpointX,pod2.nextCheckpointY,pod2.x,pod2.y);
            int[] laterCheckpointVector = directionVector(checkpointX[laterCheckpointId], checkpointY[laterCheckpointId], pod2.nextCheckpointX, pod2.nextCheckpointY);
            double checkpointsAngle = vectorAngle(nextCheckpointVector, laterCheckpointVector);
            int checkpointsDegree = Math.abs((int)Math.toDegrees(checkpointsAngle));
            checkpointsDegree = degreeCorrection(checkpointsDegree);
            
            
            
            
            // Procedures when the pod2 is approaching the checkpoint (reducing the thrust and pod direction correction)
            if ((((((double)(pod2.speed)*0.8) / (double)pod2.getCheckpointDistance()) > 0.14)&& (pod2.getCheckpointDistance()<2500)) || ((pod2.speed>500) && pod2.getCheckpointDistance()<3000)){
                
                // Diactiving the aim to later checkpoint mode if the pod is not going to pass the next checkpoint
                if(aimToLaterCheckpointIsActive){
                    if((pathDeviationDegree * pod2.getCheckpointDistance()) > 40000 || pod2.speed < 200){
                        aimToLaterCheckpointIsActive = false;
                    }
                }
                
                // This block will get activated if the angle between pod to next checkpoint and next checkpoint to later checkpoint is bigger than 120
                if(checkpointsDegree >= 120){
                    if((pathDeviationDegree < 15) || aimToLaterCheckpointIsActive){
                        aimToLaterCheckpointIsActive = true;
                        pod2.setGoTo(laterCheckpointX, laterCheckpointY);
                        if((((double)(pod2.speed)*0.8) / (double)pod2.getCheckpointDistance()) > 0.16){
                            pod2.thrust = 0;
                        }
                        if((pathDeviationDegree>=15) || pod2.getCheckpointDistance() < 1500){
                            pod2.thrust = 0;
                        }
                    }
                }
                
                // This block will get activated if the angle between pod to next checkpoint and next checkpoint to later checkpoint is 90 < angle < 120
                if(checkpointsDegree < 120 && checkpointsDegree >= 90){
                    if((pathDeviationDegree < 15) || aimToLaterCheckpointIsActive){
                        aimToLaterCheckpointIsActive = true;
                        pod2.setGoTo(laterCheckpointX, laterCheckpointY);
                        if((pathDeviationDegree>=15) || pod2.getCheckpointDistance() < 1500){
                            pod2.thrust = 0;
                        }
                    }
                }
                
                // This block will get activated if the angle between pod to next checkpoint and next checkpoint to later checkpoint is 75 < angle < 90
                if(checkpointsDegree < 90 && checkpointsDegree >= 75){
                    if((pathDeviationDegree < 15) || aimToLaterCheckpointIsActive){
                        aimToLaterCheckpointIsActive = true;
                        pod2.setGoTo(laterCheckpointX, laterCheckpointY);
                        if (pod2.getCheckpointDistance() < 2000){
                            pod2.thrust = (int)(100/(2000/(double)pod2.getCheckpointDistance()));
                            if (pod2.thrust > 100)
                                pod2.thrust=100;
                        }
                        if((pod2.angleWithNextCheckpoint>=45) || pod2.getCheckpointDistance() < 1000){
                            pod2.thrust = 0;
                        }
                    }
                }
                
                // This block will get activated if the angle between pod to next checkpoint and next checkpoint to later checkpoint is 50 < angle < 75
                if(checkpointsDegree < 75 && checkpointsDegree >= 50){
                    if((pathDeviationDegree < 15) || aimToLaterCheckpointIsActive){
                        aimToLaterCheckpointIsActive = true;
                        pod2.setGoTo(laterCheckpointX, laterCheckpointY);
                    }
                    if((pod2.angleWithNextCheckpoint>=45) || pod2.getCheckpointDistance() < 1000){
                        pod2.thrust = 60;
                    }
                }
                
                // This block will get activated if the angle between pod to next checkpoint and next checkpoint to later checkpoint is angle < 50
                if(checkpointsDegree < 50){
                    if((pathDeviationDegree < 15) || aimToLaterCheckpointIsActive){
                        aimToLaterCheckpointIsActive = true;
                        pod2.setGoTo(laterCheckpointX, laterCheckpointY);
                    }
                    if((pod2.angleWithNextCheckpoint>=45) || pod2.getCheckpointDistance() < 1000){
                        pod2.thrust = 80;
                    }
                }
                
                
                if(pod2.getCheckpointDistance() < 1000 && pod2.angleWithLaterCheckpoint < 25 && pod2.speed>200 && pathDeviationDegree<15){
                    pod2.thrust = 100;
                }
            }
            
           
            
            
            
            // Calculating the angle between vector from pod to destination with movement vector
            int[] goToVector = directionVector(pod2.goToX, pod2.goToY, pod2.x, pod2.y);
            double movementAngle = vectorAngle(new int[]{pod2.vx,pod2.vy}, goToVector);
            int movementDegree = Math.abs((int)Math.toDegrees(movementAngle));
            movementDegree = degreeCorrection(movementDegree);
            movementAngle = angleCorrection(movementAngle);
            
            
            // pod destination modification
            if ((turn > 0) && (movementDegree<90) ){
                int[] newDes = newDestination(goToVector[0], goToVector[1], pod2.x, pod2.y, movementAngle/2);
                pod2.setGoTo(newDes[0], newDes[1]);
            }
            
            
            
            // Finding the proceeding opponent pod
            if (op1.isBeating(op2)){
                succeedingOpponentPod = op1;
            } else{
                succeedingOpponentPod = op2;
            }
            
            
            // Finding destination for pod1 (blocking pod)
            int tempDes = succeedingOpponentPod.nextCheckPointId + 2;
            if (tempDes >= checkpointCount){
                tempDes = tempDes - checkpointCount;
            }
            
            
            // Handling the pod1 which is responsible to block and hit the opponents
            // States:
            //              0: searching for new block point
            //              1: going toward block point
            //              2: reached blockpoint and waitingfor opponenet
            //              3: going to hit the opponent
            switch(pod1State){
                case 0:
                    pod1.nextCheckPointId = tempDes;
                    pod1.setCheckpointXY(checkpointX[tempDes]+500, checkpointY[tempDes]+500);
                    pod1.setGoTo(checkpointX[tempDes]+500, checkpointY[tempDes]+500);
                    pod1State = 1;
                    break;
                
                case 1:
                    if(pod1.getCheckpointDistance() < 1000){
                        pod1.thrust = 0;
                        pod1.setGoTo(succeedingOpponentPod.x+succeedingOpponentPod.vx, succeedingOpponentPod.y+succeedingOpponentPod.vy);
                        pod1State = 2;
                        if (pod1.nextCheckPointId == succeedingOpponentPod.nextCheckPointId)
                            pod1State = 3;
                    }else if(pod1.getCheckpointDistance() < 4000){
                        pod1.thrust = (int)((((double)pod1.getCheckpointDistance())/(pod1.speed)) *7);
                        if (pod1.thrust > 100)
                            pod1.thrust = 100;
                    }
                    break;
                
                case 2:
                    pod1.thrust = 0;
                    pod1.setGoTo(succeedingOpponentPod.x+succeedingOpponentPod.vx, succeedingOpponentPod.y+succeedingOpponentPod.vy);
                    if (pod1.nextCheckPointId == succeedingOpponentPod.nextCheckPointId)
                            pod1State = 3;
                    break;
                case 3:
                    pod1.setCheckpointXY(succeedingOpponentPod.x+succeedingOpponentPod.vx, succeedingOpponentPod.y+succeedingOpponentPod.vy);
                    pod1.setGoTo(succeedingOpponentPod.x+succeedingOpponentPod.vx, succeedingOpponentPod.y+succeedingOpponentPod.vy);
                    pod1.thrust = 100;
                    
                    if(pod1.isGoingToCollideWith(succeedingOpponentPod)){
                        pod1.strThrust = "SHIELD";
                        pod1State = 0;
                    }
                    
                    
                    // Checking if opponent could flee from collision
                    int tempNextCheckpointId = pod1.nextCheckPointId + 1;
                    if(tempNextCheckpointId >= checkpointCount)
                        tempNextCheckpointId -= checkpointCount;
                    if(succeedingOpponentPod.nextCheckPointId == tempNextCheckpointId)
                        pod1State = 0;
                    break;
            }
            
            
            
            int pod1DestAngle = calculatePodCheckpointAngle(pod1.x, pod1.y, pod1.goToX, pod1.goToY, pod1.angle);
            if(pod1DestAngle > 45){
                if (pod1.thrust > 20)
                    pod1.thrust=20;
                else
                    pod1.thrust=0;
            }
            
            
            // Activating the shield if needed
            if (pod1.isGoingToCollideWith(op1))
                pod1.strThrust = "SHIELD";
            if (pod1.isGoingToCollideWith(op2))
                pod1.strThrust = "SHIELD";
            
            if(pod2.isGoingToCollideWith(op1) && pod2.getCheckpointDistance() < 3500)
                pod2.strThrust = "SHIELD";
            if(pod2.isGoingToCollideWith(op2) && pod2.getCheckpointDistance() < 3500)
                pod2.strThrust = "SHIELD";
            
            // Activating the boost for pod2
            if(!boostUsed && (pathDeviationDegree <= 15) && pod2.strThrust!="SHIELD"){
                if((lap==1) && (pod2.getCheckpointDistance() >6000)){
                    pod2.strThrust = "BOOST";
                    boostUsed = true;
                }
                if((lap==2) && (pod2.getCheckpointDistance() >4500)){
                    pod2.strThrust = "BOOST";
                    boostUsed = true;
                }
                if((lap==3) && (pod2.getCheckpointDistance() >3000)){
                    pod2.strThrust = "BOOST";
                    boostUsed = true;
                }
            }
            
            
            
            if(pod1.strThrust != "SHIELD")
                pod1.strThrust = String.valueOf(pod1.thrust);
            
            if(pod2.strThrust != "SHIELD" && pod2.strThrust != "BOOST")
                pod2.strThrust = String.valueOf(pod2.thrust);
            
            
            
            System.out.println(pod1.goToX + " " + pod1.goToY + " " + pod1.strThrust);
            System.out.println(pod2.goToX + " " + pod2.goToY + " " + pod2.strThrust);
            
            turn ++;
            
            op1.previousOpponentDistanceFromPod1 = op1.currentOpponentDistanceFromPod1;
            op2.previousOpponentDistanceFromPod1 = op2.currentOpponentDistanceFromPod1;
            op1.previousOpponentDistanceFromPod2 = op1.currentOpponentDistanceFromPod2;
            op2.previousOpponentDistanceFromPod2 = op2.currentOpponentDistanceFromPod2;
        }
    }
}

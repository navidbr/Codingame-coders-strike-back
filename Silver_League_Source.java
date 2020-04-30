


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
	int previousX = 0;
    int previousY = 0;
	int nextCheckpointX;
    int nextCheckpointY;
    int nextCheckpointDist;
    int nextCheckpointAngle;
	int opponentX;
	int opponentY;
	int speed;
	int goToX;
    int goToY;
	int thrust;
    String strThrust;
    int currentOpponentDistance;
    int previousOpponentDistance;
	
	
    public Pod(){
        x = 0;
		y = 0;
		previousX = 0;
		previousY = 0;
		nextCheckpointX = 0;
		nextCheckpointY = 0;
		nextCheckpointDist = 0;
		nextCheckpointAngle = 0;
		opponentX = 0;
		opponentY = 0;
		speed = 0;
        goToX = 0;
        goToY = 0;
        thrust = 100;
        strThrust = "100";
        currentOpponentDistance = 0;
        previousOpponentDistance = 0;
    }
    
	// Calculate the pod speed
    public void speedCalculate(){
        speed = (int)Math.sqrt((Math.pow(x - previousX, 2) + Math.pow(y - previousY, 2)));
    }
    
	// Set values to goToX and goToY variables
    public void setGoTo(int x, int y){
        goToX = x;
        goToY = y;
    }
    
    // Calculate the distance of my pod and opponent
    public void calculateCurrentOpponentDistance(){
        currentOpponentDistance = (int)Math.sqrt((Math.pow(x - opponentX, 2) + Math.pow(y - opponentY, 2)));
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
        
        int turn = 0;
		boolean firstLap = true;
		
		boolean aimToLaterCheckpointIsActive = false;
        

        
        List<String> checkPoints = new ArrayList<String>();
        int listIterator = 0;
        int checkPointsNumber = 0;
        
        
		Pod pod = new Pod();
        
        // game loop
        while (true) {
            pod.x = in.nextInt(); // x position of your pod
            pod.y = in.nextInt(); // y position of your pod
            pod.nextCheckpointX = in.nextInt(); // x position of the next check point
            pod.nextCheckpointY = in.nextInt(); // y position of the next check point
            pod.nextCheckpointDist = in.nextInt();
            pod.nextCheckpointAngle = Math.abs(in.nextInt());
            
            pod.opponentX = in.nextInt();
            pod.opponentY = in.nextInt();
            
            pod.thrust = 100;
            pod.strThrust = "100";
            
            
            // value initialization
            if (turn == 0){
                pod.previousX = pod.x;
                pod.previousY = pod.y;
                checkPoints.add(String.valueOf(pod.nextCheckpointX)+","+String.valueOf(pod.nextCheckpointY));
                checkPointsNumber++;
            }
            
            // checking if we are in first lap or not
            if ((checkPoints.size()>1) && (checkPoints.get(0)).equals(String.valueOf(pod.nextCheckpointX)+","+String.valueOf(pod.nextCheckpointY))){
				firstLap=false;
            }
            
            
            // remembering the checkpoints
            if (firstLap && !((checkPoints.get(checkPoints.size()-1)).equals(String.valueOf(pod.nextCheckpointX)+","+String.valueOf(pod.nextCheckpointY))) ){
                checkPoints.add(String.valueOf(pod.nextCheckpointX)+","+String.valueOf(pod.nextCheckpointY));
                checkPointsNumber++;
            }
            
            
            
            
            
            // Calculating current speed
            pod.speedCalculate();
            
            // Calculating current opponent distance
            pod.calculateCurrentOpponentDistance();
            
            pod.setGoTo(pod.nextCheckpointX, pod.nextCheckpointY);
            
            
            
            // Calculating the angle of vector from pod to next checkpoint with movement vector
            int[] playerMovementVector = directionVector(pod.x,pod.y,pod.previousX, pod.previousY);
            int[] destinationVector = directionVector(pod.nextCheckpointX,pod.nextCheckpointY,pod.x,pod.y);
            double pathDeviationAngle = vectorAngle(playerMovementVector, destinationVector);
            int pathDeviationDegree = Math.abs((int)Math.toDegrees(pathDeviationAngle));
            pathDeviationDegree = degreeCorrection(pathDeviationDegree);
            pathDeviationAngle = angleCorrection(pathDeviationAngle);
            
            
            
            // Thrust adjustment for pod
            if (pod.nextCheckpointDist > 4000){
                if (pod.nextCheckpointAngle  > 60){
                    
                    pod.thrust = (int)((100 * ((120 - pod.nextCheckpointAngle ))) / 60);
                }
                if (pod.nextCheckpointAngle  > 120)
                    pod.thrust = 0;
            }else if (pod.nextCheckpointDist > 3000){    
                if (pod.nextCheckpointAngle  > 90)
                    pod.thrust = 50;
                if (pod.nextCheckpointAngle  > 120)
                    pod.thrust = 0;
            }else if (pod.nextCheckpointDist > 2000){
                if (pod.nextCheckpointAngle  > 60)
                    pod.thrust = 60;
                if (pod.nextCheckpointAngle  > 90)
                    pod.thrust = 40;
                if (pod.nextCheckpointAngle  > 120)
                    pod.thrust = 0;
            }else if (pod.nextCheckpointDist > 1500){
                if (pod.nextCheckpointAngle  > 30)
                    pod.thrust = 50;
                if (pod.nextCheckpointAngle  > 45)
                    pod.thrust = 20;
                if (pod.nextCheckpointAngle  > 90)
                    pod.thrust = 0;
            }else if (pod.nextCheckpointDist < 1500){
                if (pod.speed > 300)
                    pod.thrust = 40;
                if (pod.speed > 400)
                    pod.thrust = 20;
                if (pod.nextCheckpointAngle  > 30)
                    pod.thrust = 20;
                if (pod.nextCheckpointAngle  > 45)
                    pod.thrust = 5;
                if (pod.nextCheckpointAngle  > 90)
                    pod.thrust = 0;
            }
            
            
            
            
            if (!firstLap){
            // We are in second or higher laps and we now have all the checkpoint positions
                
                // Finding later checkpoint position
                int laterCheckPointIndex = checkPoints.indexOf(String.valueOf(pod.nextCheckpointX)+","+String.valueOf(pod.nextCheckpointY));
                laterCheckPointIndex++;
                if (laterCheckPointIndex >= checkPointsNumber)
                    laterCheckPointIndex = 0;
                String temp = checkPoints.get(laterCheckPointIndex);
                int laterCheckpointX = Integer.valueOf(temp.split(",")[0]);
                int laterCheckpointY = Integer.valueOf(temp.split(",")[1]);
                
                
                // Calculating angle between current and later checkpoints
                int[] currentCheckpointVector = directionVector(pod.nextCheckpointX,pod.nextCheckpointY,pod.x,pod.y);
                int[] cnextCheckpointVector = directionVector(laterCheckpointX,laterCheckpointY,pod.x,pod.y);
                double checkpointsAngle = vectorAngle(currentCheckpointVector, cnextCheckpointVector);
                int degreeCheckpointsAngle = Math.abs((int)Math.toDegrees(checkpointsAngle));
                degreeCheckpointsAngle = degreeCorrection(degreeCheckpointsAngle);
                
                
                // Modifying the destination of pod to be farther from later checkpoint by 150 units
                int[] laterToNextVector = directionVector(pod.nextCheckpointX, pod.nextCheckpointY, laterCheckpointX, laterCheckpointY);
                double length = Math.sqrt(Math.pow(laterToNextVector[0],2) + Math.pow(laterToNextVector[1],2));
                laterToNextVector[0] = (int)((laterToNextVector[0]/length)*(length+150));
                laterToNextVector[1] = (int)((laterToNextVector[1]/length)*(length+150));
                pod.setGoTo(laterToNextVector[0] + laterCheckpointX, laterToNextVector[1] + laterCheckpointY);
                    
                
                // Procedures when the pod is approaching the checkpoint (reducing the thrust and pod direction correction)
                if ((((((double)(pod.speed)*0.8) / (double)pod.nextCheckpointDist) > 0.17)&& (pod.nextCheckpointDist<2500))){
                    
                    // Diactiving the aim to later checkpoint mode if the pod is not going to pass the next checkpoint
                    if(aimToLaterCheckpointIsActive){
                        if((pathDeviationDegree * pod.nextCheckpointDist) > 40000 || pod.speed < 200){
                            aimToLaterCheckpointIsActive = false;
                        }
                    }
                    
                    // This block will get activated if the angle between pod to next checkpoint and next checkpoint to later checkpoint is bigger than 120
                    if(degreeCheckpointsAngle >= 120){
                        System.err.println(">120 Active");
                        if((pathDeviationDegree < 15) || aimToLaterCheckpointIsActive){
                            aimToLaterCheckpointIsActive = true;
                            pod.setGoTo(laterCheckpointX, laterCheckpointY);
                            if((((double)(pod.speed)*0.8) / (double)pod.nextCheckpointDist) > 0.16){
                                pod.thrust = 0;
                            }
                            if((pathDeviationDegree>=15) || pod.nextCheckpointDist < 1500){
                                pod.thrust = 0;
                            }
                        }
                    }
                    
                    // This block will get activated if the angle between pod to next checkpoint and next checkpoint to later checkpoint is 90 < angle < 120
                    if(degreeCheckpointsAngle < 120 && degreeCheckpointsAngle >= 90){
                        System.err.println(">90 Active");
                        if((pathDeviationDegree < 15) || aimToLaterCheckpointIsActive){
                            aimToLaterCheckpointIsActive = true;
                            pod.setGoTo(laterCheckpointX, laterCheckpointY);
                            if((pathDeviationDegree>=15) || pod.nextCheckpointDist < 1500){
                                pod.thrust = 0;
                            }
                        }
                    }
                    
                    // This block will get activated if the angle between pod to next checkpoint and next checkpoint to later checkpoint is 75 < angle < 90
                    if(degreeCheckpointsAngle < 90 && degreeCheckpointsAngle >= 75){
                        if((pathDeviationDegree < 15) || aimToLaterCheckpointIsActive){
                            aimToLaterCheckpointIsActive = true;
                            pod.setGoTo(laterCheckpointX, laterCheckpointY);
                            if (pod.nextCheckpointDist < 2000){
                                pod.thrust = (int)(100/(2000/(double)pod.nextCheckpointDist));
                                if (pod.thrust > 100)
                                    pod.thrust=100;
                            }
                            if((pod.nextCheckpointAngle>=45) || pod.nextCheckpointDist < 1000){
                                pod.thrust = 0;
                            }
                        }
                    }
                    
                    // This block will get activated if the angle between pod to next checkpoint and next checkpoint to later checkpoint is 50 < angle < 75
                    if(degreeCheckpointsAngle < 75 && degreeCheckpointsAngle >= 50){
                        if((pathDeviationDegree < 15) || aimToLaterCheckpointIsActive){
                            aimToLaterCheckpointIsActive = true;
                            pod.setGoTo(laterCheckpointX, laterCheckpointY);
                        }
                        if((pod.nextCheckpointAngle>=45) || pod.nextCheckpointDist < 1000){
                            pod.thrust = 60;
                        }
                    }
                    
                    // This block will get activated if the angle between pod to next checkpoint and next checkpoint to later checkpoint is angle < 50
                    if(degreeCheckpointsAngle < 50){
                        if((pathDeviationDegree < 15) || aimToLaterCheckpointIsActive){
                            aimToLaterCheckpointIsActive = true;
                            pod.setGoTo(laterCheckpointX, laterCheckpointY);
                        }
                        if((pod.nextCheckpointAngle>=45) || pod.nextCheckpointDist < 1000){
                            pod.thrust = 80;
                        }
                    }
                    
                    
                    if(pod.nextCheckpointDist < 1000 && pod.nextCheckpointAngle < 25 && pod.speed>200 && pathDeviationDegree<15){
                        pod.thrust = 100;
                    }
                } 
            }
            
            
            
            // Calculating the angle between vector from pod to destination with movement vector
            int[] goToVector = directionVector(pod.goToX, pod.goToY, pod.x, pod.y);
            double movementAngle = vectorAngle(playerMovementVector, goToVector);
            int movementDegree = Math.abs((int)Math.toDegrees(movementAngle));
            movementDegree = degreeCorrection(movementDegree);
            movementAngle = angleCorrection(movementAngle);
            
            
            // pod destination modification
            if ((turn > 0) && (movementDegree<90) ){
                int[] newDes = newDestination(goToVector[0], goToVector[1], pod.x, pod.y, movementAngle/2);
                pod.setGoTo(newDes[0], newDes[1]);
            }
            
            
            
            // Activating the boost for pod
            if(!boostUsed && pathDeviationDegree <= 15){
                if((firstLap) && (pod.nextCheckpointDist >5000)){
                    
                    pod.strThrust = "BOOST";
                    boostUsed = true;
                }else if(pod.nextCheckpointDist > 3000){
                    pod.strThrust = "BOOST";
                    boostUsed = true;
                }
            }
            
            
            
            if(pod.strThrust != "BOOST")
                pod.strThrust = String.valueOf(pod.thrust);
            
            
            System.out.println(pod.goToX + " " + pod.goToY + " " + pod.strThrust);
            
            
            
            turn ++;
            pod.previousX = pod.x;
            pod.previousY = pod.y;
            pod.previousOpponentDistance = pod.currentOpponentDistance;
        }
    }
}

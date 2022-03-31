 package frc.robot;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
 
 
public class Robot extends TimedRobot {

 private static final int intakeSparkMaxID = 1;
 private static final int lowerShooterSparkMaxID = 2;
 private static final int upperShooterSparkMaxID = 3;
 private static final int rightWheel = 4;
 private static final int leftWheel = 5;

 private CANSparkMax  m_leftMotor0;
 private CANSparkMax m_rightMotor0;
 private CANSparkMax intakeMotor;
 private CANSparkMax lowerShooter;
 private CANSparkMax upperShooter;
 
 
 //Servo m_servo = new Servo(9);
 //private Timer elapsedTime = new Timer();
 
 
 private DifferentialDrive m_robotDrive;
 private final Joystick m_stick = new Joystick(0);
 private final XboxController x_stick = new XboxController(1);
 private final VictorSP inTakeSecondLevel = new VictorSP(3);
 private final VictorSP inTakeThirdLevel = new VictorSP(4);
 
 private DoubleSolenoid solenoid = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1);

 @Override
 public void robotInit() {
 
   m_leftMotor0 = new CANSparkMax(leftWheel, MotorType.kBrushed);
   m_rightMotor0  = new CANSparkMax(rightWheel, MotorType.kBrushed);
   m_leftMotor0.setInverted(true);

   intakeMotor = new CANSparkMax(intakeSparkMaxID, MotorType.kBrushless);
   lowerShooter = new CANSparkMax(lowerShooterSparkMaxID, MotorType.kBrushed);
   upperShooter = new CANSparkMax(upperShooterSparkMaxID, MotorType.kBrushless);
 
   solenoid.set(DoubleSolenoid.Value.kForward);
 
 
 
   m_robotDrive = new DifferentialDrive(m_leftMotor0, m_rightMotor0);
 
   //CameraServer.startAutomaticCapture();
 
 }
 @Override
 public void teleopPeriodic() {
   m_robotDrive.arcadeDrive( -m_stick.getY(),m_stick.getX() );
 
   if(x_stick.getYButton()){
     intakeMotor.set(-0.25);
     inTakeSecondLevel.set(-0.6);
     inTakeThirdLevel.set(0.50);
   }else if(x_stick.getXButton()){
     intakeMotor.set(0.25);
     inTakeSecondLevel.set(0.6);
     inTakeThirdLevel.set(-0.50);
   } else {
     intakeMotor.set(0);
     inTakeSecondLevel.set(0);
     inTakeThirdLevel.set(0);
     lowerShooter.set(0);
   }
 
   if(x_stick.getAButton()){
     upperShooter.set(1);
     lowerShooter.set(1);
   } else {
     upperShooter.set(0);
     lowerShooter.set(0);
   }
 
    if (x_stick.getRightBumper()) {
       solenoid.set(DoubleSolenoid.Value.kForward);    
    }
    else if(x_stick.getLeftBumper())
    {
      solenoid.set(DoubleSolenoid.Value.kReverse);  
    }
 
   /*if(x_stick.getBButton()){
     m_servo.set(.5);
   } else if(x_stick.getAButton()){
     elapsedTime.reset();
     elapsedTime.start();
     while(elapsedTime.get() < 6){
     m_servo.set(1);
     }
   } else {
     m_servo.set(.5);
   }
 
 
   /*if (x_stick.getRightBumper()) {
      solenoid.set(DoubleSolenoid.Value.kForward);  
   }
   else if(x_stick.getLeftBumper())
   {
     solenoid.set(DoubleSolenoid.Value.kReverse);
   }
   */
 }
}
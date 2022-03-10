// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.smartdashboard.*;
//import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import org.opencv.core.Point;
import edu.wpi.first.vision.VisionThread;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.PneumaticsControlModule;
import edu.wpi.first.wpilibj.DoubleSolenoid;



/**
 * This is a demo program showing the use of the DifferentialDrive class. Runs the motors with
 * arcade steering.
 */

public class Robot extends TimedRobot{
  private final PWMSparkMax m_leftMotor0 = new PWMSparkMax(0);
  private final VictorSP m_leftMotor1 = new VictorSP(1);
    private final MotorControllerGroup m_leftDriveMotors = new MotorControllerGroup(m_leftMotor0, m_leftMotor1);
  private final PWMSparkMax m_rightDriveMotors = new PWMSparkMax(2);
  private final PWMSparkMax m_intakeMotor = new PWMSparkMax(3);
  private final PWMSparkMax m_shooterMotor = new PWMSparkMax(4);

  private final PneumaticsControlModule pCModule = new PneumaticsControlModule();


  private static final int IMG_WIDTH = 320;
  private static final int IMG_HEIGHT = 240;

  private VisionThread visionThread;
  private double centerX = 0.0;

  private final Object imgLock = new Object();
  
  private final DifferentialDrive m_robotDrive = new DifferentialDrive(m_leftDriveMotors, m_rightDriveMotors);
  private final Joystick m_stick = new Joystick(0);

  private Timer elapsedTime = new Timer();

  Thread m_visionThread;


//AnalogPotentiometer pot = new AnalogPotentiometer(0, 180, 30);

DoubleSolenoid exampleDoublePCM = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 6, 7);



  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_rightDriveMotors.setInverted(true);
    //m_shooterMotor.setInverted(true);
    pCModule.disableCompressor();

    UsbCamera camera = CameraServer.startAutomaticCapture();
    camera.setResolution(IMG_WIDTH, IMG_HEIGHT);

    visionThread = new VisionThread(camera, new yCardPipeline(), pipeline -> {
      if (!pipeline.findBlobsOutput().toList().isEmpty()) {
        
          Point r = pipeline.findBlobsOutput().toList().get(0).pt;
          synchronized (imgLock) {
              centerX = r.x;
          }
      }
  });
    visionThread.start();
  }

  @Override
  public void autonomousInit(){
    /*
      elapsedTime.reset();
      elapsedTime.start();
      /*while(elapsedTime.get() < 1){
        SmartDashboard.putString("DB/String 0", String.format("%.2f", elapsedTime.get()));
        m_robotDrive.arcadeDrive(0.5, 0);
      }

      for(int i = 0; i < 3; i++){ //wiggly wiggly
        elapsedTime.reset();

        while(elapsedTime.get() < 1){
          SmartDashboard.putString("DB/String 0", String.format("%.2f", elapsedTime.get()));
          m_robotDrive.arcadeDrive(0.5, 0.5);
        }

        while(elapsedTime.get() < 2){
          SmartDashboard.putString("DB/String 0", String.format("%.2f", elapsedTime.get()));
          m_robotDrive.arcadeDrive(0.5, -0.5);
        }
      }
      m_robotDrive.arcadeDrive(0, 0);
      */

      
  }

  @Override
  public void teleopPeriodic() {
    // Drive with arcade drive.
    // That means that the Y axis drives forward
    // and backward, and the X turns left and right.
    m_robotDrive.arcadeDrive(-m_stick.getY(), -m_stick.getX());
    
    //Intake functions
    if(m_stick.getRawButton(5)){
      m_intakeMotor.set(0.6);
    }else if(m_stick.getRawButton(3)){
      m_intakeMotor.set(-0.4);
    }else{
      m_intakeMotor.set(0);
    }

    //Shooter function
    if(m_stick.getRawButton(1)){
      m_shooterMotor.set(m_stick.getThrottle()*0.5+0.5);
    }else{
      m_shooterMotor.set(0);
    }
    SmartDashboard.putString("DB/String 0", String.format("%.2f", m_stick.getThrottle()*0.5+0.5));
    //Solenoid Functions
    if(m_stick.getRawButton(4)){
      SmartDashboard.putString("DB/String 1", "RETRACT");
      exampleDoublePCM.set(DoubleSolenoid.Value.kReverse);
    }else if(m_stick.getRawButton(6)){
      SmartDashboard.putString("DB/String 1", "FORWARD");
      exampleDoublePCM.set(DoubleSolenoid.Value.kForward);
    }else{
      SmartDashboard.putString("DB/String 1", "OFF");
      exampleDoublePCM.set(DoubleSolenoid.Value.kOff);
    }
    SmartDashboard.putString("DB/String 2", exampleDoublePCM.get().toString());
  }

  @Override
public void autonomousPeriodic() {
    double centerX;
    synchronized (imgLock) {
        centerX = this.centerX;
    }
    SmartDashboard.putString("DB/String 0", String.format("%.2f", centerX));
    double turn = centerX - (IMG_WIDTH / 2 / 4);
    SmartDashboard.putString("DB/String 1", String.format("%.2f",turn));
    m_robotDrive.arcadeDrive(0, turn * 0.002 * (m_stick.getThrottle()*0.5+0.5));
}
}
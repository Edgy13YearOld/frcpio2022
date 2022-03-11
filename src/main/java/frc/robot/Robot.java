// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
//import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.smartdashboard.*;
//import edu.wpi.first.cameraserver.CameraServer;
//import edu.wpi.first.cscore.UsbCamera;
//import org.opencv.core.Point;
//import edu.wpi.first.vision.VisionThread;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.PneumaticsControlModule;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Robot extends TimedRobot{
  //Primary Controller
  private final Joystick m_stick = new Joystick(0);
  //Motor Controllers
  private final PWMSparkMax m_leftMotor0 = new PWMSparkMax(0);
  private final VictorSP m_leftMotor1 = new VictorSP(1);
  private final MotorControllerGroup m_leftDriveMotors = new MotorControllerGroup(m_leftMotor0, m_leftMotor1);
  private final PWMSparkMax m_rightDriveMotors = new PWMSparkMax(2);
  private final PWMSparkMax m_intakeMotor = new PWMSparkMax(3);
  private final PWMSparkMax m_shooterMotor = new PWMSparkMax(4);
  private final VictorSP m_conveyerMotor = new VictorSP(5);
  //Drivetrain
  private final DifferentialDrive m_robotDrive = new DifferentialDrive(m_leftDriveMotors, m_rightDriveMotors);
  //Pneumatics
  private final PneumaticsControlModule pCModule = new PneumaticsControlModule();//You wouldn't believe what this is
  DoubleSolenoid intakePneumatic = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 7, 6);
  //Intake Management
  private int intakeDown = 0;
  //Timekeeping
  //private Timer elapsedTime = new Timer();
  //Parameters for the camera
  //private final int IMG_WIDTH = 320;
  //private final int IMG_HEIGHT = 240;
  //Tools for object recognition
  //private VisionThread visionThread;
  //private double centerX = 0.0;
  //private final Object imgLock = new Object();
  //Thread m_visionThread;

  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_rightDriveMotors.setInverted(true);
    m_conveyerMotor.setInverted(true);
    m_shooterMotor.setInverted(true);
    //pCModule.disableCompressor();
    //Camera Init
    /*
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
    */
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
  public void autonomousPeriodic() {
    //Center the robot to the yellow square
    /*
      double centerX;
      synchronized (imgLock) {
          centerX = this.centerX;
      }
      SmartDashboard.putString("DB/String 0", String.format("%.2f", centerX));
      double turn = centerX - (IMG_WIDTH / 2 / 4);
      SmartDashboard.putString("DB/String 1", String.format("%.2f",turn));
      m_robotDrive.arcadeDrive(0, turn * 0.002 * (m_stick.getThrottle()*0.5+0.5));
    */
  }

  @Override
  public void teleopPeriodic() {
    // Drive with arcade drive.
    // That means that the Y axis drives forward
    // and backward, and the X turns left and right.
    m_robotDrive.arcadeDrive(-m_stick.getY(), -m_stick.getX());
    //Intake functions
    if(m_stick.getRawButton(5)){//Button 5 rejects
      m_intakeMotor.set(0.4* intakeDown);
      m_conveyerMotor.set(0.4);
    }else if(m_stick.getRawButton(3)){//Button 3 intakes
      m_intakeMotor.set(-0.5 * intakeDown);
      m_conveyerMotor.set(-0.8);
    }else{
      m_intakeMotor.set(0);
      m_conveyerMotor.set(0);
    }
    //Shooter function
    if(m_stick.getRawButton(1)){//Trigger
      m_shooterMotor.set(-m_stick.getThrottle()*0.5+0.5);
    }else{
      m_shooterMotor.set(0);
    }
    SmartDashboard.putString("DB/String 0", String.format("SHOOTER THROTTLE %.2f", -m_stick.getThrottle()*0.5+0.5));
    //Solenoid Functions
    if(m_stick.getRawButton(4)){
      SmartDashboard.putString("DB/String 1", "INTAKE RETRACT");
      intakePneumatic.set(DoubleSolenoid.Value.kReverse);
      intakeDown = 0;
    }else if(m_stick.getRawButton(6)){
      SmartDashboard.putString("DB/String 1", "INTAKE EXTEND");
      intakePneumatic.set(DoubleSolenoid.Value.kForward);
      intakeDown = 1;
    }else{
      SmartDashboard.putString("DB/String 1", "INTAKE off");
      intakePneumatic.set(DoubleSolenoid.Value.kOff);
    }
  }
}
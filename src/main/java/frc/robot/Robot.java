// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Timer;
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
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Robot extends TimedRobot{
  private final Joystick stick = new Joystick(0);
  private final XboxController controller = new XboxController(1);
  private final PWMSparkMax leftMotor0 = new PWMSparkMax(0);
  private final VictorSP leftMotor1 = new VictorSP(1);
  final JoystickButton button = new JoystickButton(controller, 6);
  private final MotorControllerGroup leftDriveMotors = new MotorControllerGroup(leftMotor0, leftMotor1);
  private final PWMSparkMax rightDriveMotors = new PWMSparkMax(2);
  public static final int intakeSparkMaxID = 1;
  private CANSparkMax intakeMotor;
  //private final PWMSparkMax m_intakeMotor = new PWMSparkMax(3);
  //private final PWMSparkMax m_shooterMotor = new PWMSparkMax(4);
  //private final VictorSP m_conveyerMotor = new VictorSP(5);
  //Drivetrain
  private final DifferentialDrive m_robotDrive = new DifferentialDrive(leftDriveMotors, rightDriveMotors);
  //Pneumatics
  private final PneumaticsControlModule pCModule = new PneumaticsControlModule();//You wouldn't believe what this is
  DoubleSolenoid intakePneumatic = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 7, 6);
  //Intake Management
  private int intakeDown = 0;
  //Timekeeping
  private Timer elapsedTime = new Timer();
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
    rightDriveMotors.setInverted(true);
    intakeMotor = new CANSparkMax(intakeSparkMaxID, MotorType.kBrushless);
    //m_conveyerMotor.setInverted(true);
    //m_shooterMotor.setInverted(true);
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
    m_robotDrive.arcadeDrive(-stick.getY(), -stick.getX());

    
    if(stick.getRawButton(1)){
      intakeMotor.set(-0.25);
      SmartDashboard.putString("DB/String 1", "poo");
    } else {
      intakeMotor.set(0);
    }
    

    
    if(controller.getRawButton(6)){
      intakeMotor.set(-0.25);
      SmartDashboard.putString("DB/String 1", "intake on");
    } else {
      intakeMotor.set(0);
      SmartDashboard.putString("DB/String 1", "intake off");
    }
    
    
    //Intake functions
    /*if(m_stick.getRawButton(5)){//Button 5 rejects
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
    */
    if(stick.getRawButton(4)){
      SmartDashboard.putString("DB/String 1", "INTAKE RETRACT");
      intakePneumatic.set(DoubleSolenoid.Value.kReverse);
      intakeDown = 0;
    }else if(stick.getRawButton(6)){
      SmartDashboard.putString("DB/String 1", "INTAKE EXTEND");
      intakePneumatic.set(DoubleSolenoid.Value.kForward);
      intakeDown = 1;
    }else{
      SmartDashboard.putString("DB/String 1", "INTAKE off");
      intakePneumatic.set(DoubleSolenoid.Value.kOff);
    }
    
  }
}
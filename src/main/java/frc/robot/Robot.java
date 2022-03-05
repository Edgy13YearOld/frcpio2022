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
//import com.revrobotics.CANSparkMax;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import org.opencv.core.Point;
import edu.wpi.first.vision.VisionThread;



/**
 * This is a demo program showing the use of the DifferentialDrive class. Runs the motors with
 * arcade steering.
 */

public class Robot extends TimedRobot{
  private final PWMSparkMax m_leftMotor0 = new PWMSparkMax(0);
  private final PWMSparkMax m_rightMotor0 = new PWMSparkMax(1);
  //private final PWMSparkMax m_motor;

  private static final int IMG_WIDTH = 320;
  private static final int IMG_HEIGHT = 240;

  private VisionThread visionThread;
  private double centerX = 0.0;
  private DifferentialDrive drive;

  private final Object imgLock = new Object();
  
  private final DifferentialDrive m_robotDrive = new DifferentialDrive(m_leftMotor0, m_rightMotor0);
  private final Joystick m_stick = new Joystick(0);

  //private Timer elapsedTime = new Timer();

  Thread m_visionThread;

  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_rightMotor0.setInverted(true);

    UsbCamera camera = CameraServer.startAutomaticCapture();
    camera.setResolution(IMG_WIDTH, IMG_HEIGHT);

    visionThread = new VisionThread(camera, new MyVisionPipeline(), pipeline -> {
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
    
  }

  @Override
public void autonomousPeriodic() {
    double centerX = IMG_WIDTH / 2;
    synchronized (imgLock) {
        centerX = this.centerX;
    }
    SmartDashboard.putString("DB/String 0", String.format("%.2f", centerX));
    SmartDashboard.putString("DB/String 1", String.format("%d",IMG_WIDTH / 2));
    double turn = centerX - (IMG_WIDTH / 2);
    SmartDashboard.putString("DB/String 2", String.format("%.2f",turn));
    m_robotDrive.arcadeDrive(0, turn * 0.000);
}
}
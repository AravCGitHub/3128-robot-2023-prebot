package frc.team3128.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ScheduleCommand;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.team3128.RobotContainer;
import frc.team3128.common.hardware.input.NAR_XboxController;
import frc.team3128.common.narwhaldashboard.NarwhalDashboard;
import frc.team3128.subsystems.Led;
import frc.team3128.subsystems.Wrist;
import frc.team3128.subsystems.Manipulator;
import frc.team3128.subsystems.Vision;

public class CmdManager {
    private static Led led = Led.getInstance();
    private static Wrist wrist = Wrist.getInstance();
    private static Manipulator manipulator = Manipulator.getInstance();
    private static NAR_XboxController controller = RobotContainer.controller;


    private CmdManager() {}

    public static CommandBase CmdWrist(double setpoint) {
        return Commands.sequence(
            new InstantCommand(()-> wrist.startPID(setpoint), wrist),
            new WaitUntilCommand(()-> wrist.atSetpoint())
        );
    }

    public static CommandBase CmdManipIntake(Boolean cone) {
        return new InstantCommand(()-> manipulator.intake(cone), manipulator);
    }

    public static CommandBase CmdManipOuttake() {
        return new InstantCommand(()-> manipulator.outtake(), manipulator);
    }

    public static CommandBase CmdStopManip() {
        return new InstantCommand(()-> manipulator.stopRoller(), manipulator);
    }

    public static CommandBase vibrateController() {
        return new ScheduleCommand(new WaitCommand(0.5).deadlineWith(new StartEndCommand(() -> controller.startVibrate(), () -> RobotContainer.controller.stopVibrate())));
    }
}
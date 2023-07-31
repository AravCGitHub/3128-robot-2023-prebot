package frc.team3128.commands;

import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.PathPoint;
import com.pathplanner.lib.commands.PPSwerveControllerCommand;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.CommandBase;
import static frc.team3128.Constants.SwerveConstants.*;
import static frc.team3128.Constants.VisionConstants.*;
import static frc.team3128.Constants.FieldConstants.*;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;

import static frc.team3128.Constants.AutoConstants.*;

import static frc.team3128.Constants.TrajectoryConstants.*;

import frc.team3128.autonomous.Trajectories;
import frc.team3128.subsystems.Swerve;

public class CmdTrajectory extends CommandBase {

    private final Swerve swerve;
    private final Pose2d endPoint;
    private CommandBase trajCommand;

    public CmdTrajectory(Pose2d endPoint) {
        swerve = Swerve.getInstance();
        this.endPoint = endPoint;
        addRequirements(swerve);
    }

    private ArrayList<Pose2d> generatePoses() {
        final ArrayList<Pose2d> poses = new ArrayList<Pose2d>();
        final Translation2d start = swerve.getPose().getTranslation();
        final Rotation2d holonomicAngle = endPoint.getRotation();
        poses.add(new Pose2d(start, swerve.getGyroRotation2d()));
        if (!pastPoint(start, CONDITION_1)) poses.add(allianceFlip(new Pose2d(POINT_1, holonomicAngle)));
        if (!pastPoint(start, CONDITION_2)) poses.add(allianceFlip(new Pose2d(start.nearest(POINT_2), holonomicAngle)));
        if (!pastPoint(start, CONDITION_3)) poses.add(allianceFlip(new Pose2d(start.nearest(POINT_3), holonomicAngle)));
        poses.add(allianceFlip(endPoint));
        return poses;
    }


    private ArrayList<PathPoint> generatePoints() {
        final ArrayList<PathPoint> points = new ArrayList<PathPoint>();
        final ArrayList<Pose2d> positions = generatePoses();

        final Rotation2d heading = Rotation2d.fromDegrees(180);
        for (int i = 0; i < positions.size(); i++) {
            final Pose2d pose = positions.get(i);
            final PathPoint point = new PathPoint(pose.getTranslation(), heading, pose.getRotation());
            points.add(point);
        }

        return points;
    }

    private boolean pastPoint(Translation2d start, double condition) {
        if (DriverStation.getAlliance() == Alliance.Blue) {
            return start.getX() < condition;
        }
        return start.getX() > FIELD_X_LENGTH - condition;
    }

    @Override
    public void initialize() {
        final PathPlannerTrajectory trajectory = PathPlanner.generatePath(
            pathConstraints, generatePoints()
        );
        trajCommand = Trajectories.generateAuto(trajectory);
        trajCommand.schedule();
    }

    @Override
    public void execute() {

    }

    @Override
    public boolean isFinished() {
        return !trajCommand.isScheduled();
    }

    @Override
    public void end(boolean interrupted) {
        swerve.stop();
    }
}

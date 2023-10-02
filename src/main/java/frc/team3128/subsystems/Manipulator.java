package frc.team3128.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.team3128.common.hardware.motorcontroller.NAR_TalonSRX;
import frc.team3128.common.utility.NAR_Shuffleboard;
import static frc.team3128.Constants.ManipulatorConstants.*;
import com.ctre.phoenix.motorcontrol.NeutralMode;

public class Manipulator extends SubsystemBase {

    public NAR_TalonSRX m_roller;

    private static Manipulator instance;

    public static boolean isCone = true;

    public boolean isOuttaking = false;

    public static synchronized Manipulator getInstance() {
        if (instance == null){
            instance = new Manipulator();  
        }
        
        return instance;
    }

    public Manipulator(){
        configMotor();
        initShuffleboard();
    }

    private void configMotor(){
        m_roller = new NAR_TalonSRX(ROLLER_MOTOR_ID);
        m_roller.setInverted(true);
        m_roller.setNeutralMode(NeutralMode.Brake);
    }

    public void intake(boolean cone) {
        isOuttaking = false;
        isCone = cone;
        if (isCone) reverse();
        else forward();
    }    

    public void outtake(){
        isOuttaking = true;
        if (!isCone) reverse();
        else forward();
    }

    public void stallPower() {
        set(isCone ? -STALL_POWER_CONE : STALL_POWER_CUBE);
    }

    public void set(double power){
        m_roller.set(power);
    }

    public void forward(){
        m_roller.set(ROLLER_POWER);
    }

    public void reverse(){
        m_roller.set(-ROLLER_POWER);
    }

    public void stopRoller(){
        m_roller.set(0);
    }

    public boolean hasObjectPresent(){
        return isCone ? Math.abs(getCurrent()) > CURRENT_THRESHOLD_CONE : Math.abs(getCurrent()) > CURRENT_THRESHOLD_CUBE;
    }

    public double getCurrent(){
        return m_roller.getStatorCurrent();
    }
    
    public void initShuffleboard() {
        NAR_Shuffleboard.addData("Manipulator", "Manip current", () -> getCurrent(), 0, 1);
        NAR_Shuffleboard.addData("Manipulator", "get", () -> m_roller.getMotorOutputPercent(), 0, 3);
        NAR_Shuffleboard.addData("Manipulator", "ObjectPresent", ()-> hasObjectPresent(), 1, 1);
    }
}
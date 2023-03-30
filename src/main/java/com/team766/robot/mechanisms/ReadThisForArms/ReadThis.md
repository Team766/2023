<h1> Overview and features of Swingy_Arm </h1>
  <h2> PID </h2>
    <h3> SmartMotion </h3>
    The <strong> PID </strong> for both of the arms is <strong> REV Robotics SmartMotion PID</strong>. This runs on the rev spark max motorcontroller, which saves memory on the roborio. To tune <strong> SmartMotion PID</strong>, start tuning kF, then kP, and finally kD. Don't tune kI.
    The values must be changed in code, not in the config file (yet, maybe a summer project), by SparkMaxPIDController.setFF(),.setP(), .setD(), you get the idea. 
    <h3> Switching States </h3>
    When we use the <strong> PID </strong>, we added a buffer of 0.004 absolute encoder units. <strong> (oh and i frogot to mention, the PID works on the absolute encoders)</strong>.
    This works in a simple if else loop, and then another loop inside of the if else which only runs if the if is statisfied. The first if loop asks if lastPosition is different. If the new value is different than the lastPosition, then the inside if else will run. Otherwise, the code will apply a steady stream of power to keep the arm where it is. 
    The next loop asks if the arm position has hit the correct height, with the buffer. If not, it will run the <strong> PID</strong>. If it has, it will change the feedback device of the motorcontroller to the onboard encoder and then will update lastPosition. This is the same for both arms.
    <h4> So yeah, that's about it. Remember to declare your PID variables in the top and lastPosition. Also add your deadzone. Its about 6 variables per arm, and then the deadzone, so 7. Happy coding!</h4>
    

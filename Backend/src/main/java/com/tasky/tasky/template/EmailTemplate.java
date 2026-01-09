package com.tasky.tasky.template;

import com.tasky.tasky.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplate {

    @Autowired
    private EmailService emailService;

    public String buildWelcomeEmailTemplate(String organizationName) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f9f9f9;">
                <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    
                    <!-- Header -->
                    <div style="background-color: #4a90e2; padding: 40px 30px; text-align: center;">
                        <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: normal;">
                            Welcome to Tasky
                        </h1>
                        <p style="color: #e6f3ff; margin: 10px 0 0 0; font-size: 16px;">
                            Task Management Made Simple
                        </p>
                    </div>
                    
                    <!-- Content -->
                    <div style="padding: 40px 30px;">
                        <h2 style="color: #333; margin: 0 0 20px 0; font-size: 22px;">
                            Hello %s!
                        </h2>
                        
                        <p style="margin-bottom: 25px; font-size: 16px; color: #555;">
                            Your organization has been successfully created. You're all set to start managing tasks and collaborating with your team.
                        </p>
                        
                        <!-- Simple Features -->
                        <div style="background-color: #f8f9fa; padding: 25px; border-radius: 6px; margin-bottom: 30px;">
                            <h3 style="margin: 0 0 15px 0; color: #333; font-size: 18px;">What you can do:</h3>
                            <ul style="margin: 0; padding-left: 20px; color: #666;">
                                <li style="margin-bottom: 8px;">Create and organize projects</li>
                                <li style="margin-bottom: 8px;">Assign tasks to team members</li>
                                <li style="margin-bottom: 8px;">Track progress and deadlines</li>
                                <li style="margin-bottom: 0;">Generate reports and insights</li>
                            </ul>
                        </div>
                        
                        <!-- CTA Button -->
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="#" style="background-color: #4a90e2; color: #ffffff; padding: 14px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;">
                                Get Started
                            </a>
                        </div>
                        
                        <!-- Support -->
                        <div style="border-top: 1px solid #eee; padding-top: 25px; text-align: center;">
                            <p style="margin: 0 0 10px 0; color: #666; font-size: 14px;">
                                Need help getting started?
                            </p>
                            <p style="margin: 0; color: #666; font-size: 14px;">
                                Contact us at <a href="mailto:support@tasky.com" style="color: #4a90e2;">support@tasky.com</a>
                            </p>
                        </div>
                    </div>
                    
                    <!-- Footer -->
                    <div style="background-color: #f8f9fa; padding: 20px 30px; text-align: center; border-top: 1px solid #eee;">
                        <p style="margin: 0; color: #999; font-size: 12px;">
                            © 2026 Tasky. All rights reserved.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """, organizationName);
    }

    public String buildOrganizationUpdateTemplate(String organizationName) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f9f9f9;">
                <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    
                    <!-- Header -->
                    <div style="background-color: #28a745; padding: 30px; text-align: center;">
                        <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: normal;">
                            Organization Updated
                        </h1>
                    </div>
                    
                    <!-- Content -->
                    <div style="padding: 30px;">
                        <h2 style="color: #333; margin: 0 0 15px 0; font-size: 20px;">
                            Hello %s!
                        </h2>
                        
                        <p style="margin-bottom: 20px; font-size: 16px; color: #555;">
                            Your organization information has been successfully updated. All changes are now active.
                        </p>
                        
                        <div style="background-color: #d4edda; border: 1px solid #c3e6cb; padding: 15px; border-radius: 5px; margin-bottom: 20px;">
                            <p style="margin: 0; color: #155724; font-size: 14px;">
                                ✓ Organization details updated successfully
                            </p>
                        </div>
                        
                        <p style="margin: 0; color: #666; font-size: 14px;">
                            If you have any questions, contact us at <a href="mailto:support@tasky.com" style="color: #28a745;">support@tasky.com</a>
                        </p>
                    </div>
                    
                    <!-- Footer -->
                    <div style="background-color: #f8f9fa; padding: 15px; text-align: center; border-top: 1px solid #eee;">
                        <p style="margin: 0; color: #999; font-size: 12px;">© 2026 Tasky. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, organizationName);
    }

    public String buildPasswordUpdateTemplate(String organizationName) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f9f9f9;">
                <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    
                    <!-- Header -->
                    <div style="background-color: #17a2b8; padding: 30px; text-align: center;">
                        <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: normal;">
                            Password Updated
                        </h1>
                    </div>
                    
                    <!-- Content -->
                    <div style="padding: 30px;">
                        <h2 style="color: #333; margin: 0 0 15px 0; font-size: 20px;">
                            Hello %s!
                        </h2>
                        
                        <p style="margin-bottom: 20px; font-size: 16px; color: #555;">
                            Your organization password has been successfully updated. Your account is now secured with the new password.
                        </p>
                        
                        <div style="background-color: #d1ecf1; border: 1px solid #bee5eb; padding: 15px; border-radius: 5px; margin-bottom: 20px;">
                            <p style="margin: 0; color: #0c5460; font-size: 14px;">
                                🔒 Password changed successfully
                            </p>
                        </div>
                        
                        <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin-bottom: 20px;">
                            <p style="margin: 0; color: #856404; font-size: 14px;">
                                ⚠️ If you didn't make this change, please contact support immediately.
                            </p>
                        </div>
                        
                        <p style="margin: 0; color: #666; font-size: 14px;">
                            Need help? Contact us at <a href="mailto:support@tasky.com" style="color: #17a2b8;">support@tasky.com</a>
                        </p>
                    </div>
                    
                    <!-- Footer -->
                    <div style="background-color: #f8f9fa; padding: 15px; text-align: center; border-top: 1px solid #eee;">
                        <p style="margin: 0; color: #999; font-size: 12px;">© 2026 Tasky. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, organizationName);
    }

    public String buildOrganizationSuspendTemplate(String organizationName) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f9f9f9;">
                <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    
                    <!-- Header -->
                    <div style="background-color: #ffc107; padding: 30px; text-align: center;">
                        <h1 style="color: #212529; margin: 0; font-size: 24px; font-weight: normal;">
                            Organization Suspended
                        </h1>
                    </div>
                    
                    <!-- Content -->
                    <div style="padding: 30px;">
                        <h2 style="color: #333; margin: 0 0 15px 0; font-size: 20px;">
                            Hello %s!
                        </h2>
                        
                        <p style="margin-bottom: 20px; font-size: 16px; color: #555;">
                            Your organization account has been temporarily suspended. Access to Tasky services is currently restricted.
                        </p>
                        
                        <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin-bottom: 20px;">
                            <p style="margin: 0; color: #856404; font-size: 14px;">
                                ⚠️ Account Status: Suspended
                            </p>
                        </div>
                        
                        <div style="background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin-bottom: 20px;">
                            <h3 style="margin: 0 0 10px 0; color: #333; font-size: 16px;">What this means:</h3>
                            <ul style="margin: 0; padding-left: 20px; color: #666;">
                                <li>Temporary restriction of account access</li>
                                <li>Data remains secure and intact</li>
                                <li>Account can be reactivated</li>
                            </ul>
                        </div>
                        
                        <p style="margin: 0; color: #666; font-size: 14px;">
                            For reactivation or questions, contact us at <a href="mailto:support@tasky.com" style="color: #ffc107;">support@tasky.com</a>
                        </p>
                    </div>
                    
                    <!-- Footer -->
                    <div style="background-color: #f8f9fa; padding: 15px; text-align: center; border-top: 1px solid #eee;">
                        <p style="margin: 0; color: #999; font-size: 12px;">© 2026 Tasky. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, organizationName);
    }

    public String buildOrganizationActivateTemplate(String organizationName) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f9f9f9;">
                <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    
                    <!-- Header -->
                    <div style="background-color: #28a745; padding: 30px; text-align: center;">
                        <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: normal;">
                            Organization Activated
                        </h1>
                    </div>
                    
                    <!-- Content -->
                    <div style="padding: 30px;">
                        <h2 style="color: #333; margin: 0 0 15px 0; font-size: 20px;">
                            Hello %s!
                        </h2>
                        
                        <p style="margin-bottom: 20px; font-size: 16px; color: #555;">
                            Great news! Your organization account has been successfully activated. You now have full access to all Tasky services.
                        </p>
                        
                        <div style="background-color: #d4edda; border: 1px solid #c3e6cb; padding: 15px; border-radius: 5px; margin-bottom: 20px;">
                            <p style="margin: 0; color: #155724; font-size: 14px;">
                                ✓ Account Status: Active
                            </p>
                        </div>
                        
                        <div style="text-align: center; margin: 25px 0;">
                            <a href="#" style="background-color: #28a745; color: #ffffff; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;">
                                Access Dashboard
                            </a>
                        </div>
                        
                        <p style="margin: 0; color: #666; font-size: 14px;">
                            Welcome back! Contact us at <a href="mailto:support@tasky.com" style="color: #28a745;">support@tasky.com</a> if you need assistance.
                        </p>
                    </div>
                    
                    <!-- Footer -->
                    <div style="background-color: #f8f9fa; padding: 15px; text-align: center; border-top: 1px solid #eee;">
                        <p style="margin: 0; color: #999; font-size: 12px;">© 2026 Tasky. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, organizationName);
    }

    public String buildOrganizationDeleteTemplate(String organizationName) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f9f9f9;">
                <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    
                    <!-- Header -->
                    <div style="background-color: #dc3545; padding: 30px; text-align: center;">
                        <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: normal;">
                            Organization Deleted
                        </h1>
                    </div>
                    
                    <!-- Content -->
                    <div style="padding: 30px;">
                        <h2 style="color: #333; margin: 0 0 15px 0; font-size: 20px;">
                            Hello %s!
                        </h2>
                        
                        <p style="margin-bottom: 20px; font-size: 16px; color: #555;">
                            Your organization account has been deleted as requested. We're sorry to see you go.
                        </p>
                        
                        <div style="background-color: #f8d7da; border: 1px solid #f5c6cb; padding: 15px; border-radius: 5px; margin-bottom: 20px;">
                            <p style="margin: 0; color: #721c24; font-size: 14px;">
                                ⚠️ Account Status: Deleted
                            </p>
                        </div>
                        
                        <div style="background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin-bottom: 20px;">
                            <h3 style="margin: 0 0 10px 0; color: #333; font-size: 16px;">What happens next:</h3>
                            <ul style="margin: 0; padding-left: 20px; color: #666;">
                                <li>Account access is permanently disabled</li>
                                <li>Data will be securely removed according to our policy</li>
                                <li>You can create a new account anytime</li>
                            </ul>
                        </div>
                        
                        <p style="margin: 0; color: #666; font-size: 14px;">
                            Thank you for using Tasky. Questions? Contact us at <a href="mailto:support@tasky.com" style="color: #dc3545;">support@tasky.com</a>
                        </p>
                    </div>
                    
                    <!-- Footer -->
                    <div style="background-color: #f8f9fa; padding: 15px; text-align: center; border-top: 1px solid #eee;">
                        <p style="margin: 0; color: #999; font-size: 12px;">© 2026 Tasky. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, organizationName);
    }

    public String buildPasswordResetOtpTemplate(String organizationName, int otp) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f9f9f9;">
                <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    
                    <!-- Header -->
                    <div style="background-color: #6f42c1; padding: 30px; text-align: center;">
                        <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: normal;">
                            Password Reset Request
                        </h1>
                    </div>
                    
                    <!-- Content -->
                    <div style="padding: 30px;">
                        <h2 style="color: #333; margin: 0 0 15px 0; font-size: 20px;">
                            Hello %s!
                        </h2>
                        
                        <p style="margin-bottom: 20px; font-size: 16px; color: #555;">
                            We received a request to reset your organization password. Use the OTP below to proceed with password reset.
                        </p>
                        
                        <!-- OTP Display -->
                        <div style="background-color: #f8f9fa; border: 2px solid #6f42c1; padding: 25px; border-radius: 8px; text-align: center; margin: 25px 0;">
                            <p style="margin: 0 0 10px 0; color: #666; font-size: 14px; text-transform: uppercase; letter-spacing: 1px;">
                                Your OTP Code
                            </p>
                            <div style="font-size: 32px; font-weight: bold; color: #6f42c1; letter-spacing: 8px; font-family: 'Courier New', monospace;">
                                %d
                            </div>
                            <p style="margin: 0 0 10px 0; color: #666; font-size: 14px; text-transform: uppercase; letter-spacing: 1px;">
                                Your Code will Expire in 10 Minutes
                            </p>
                        </div>
                        
                        <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin-bottom: 20px;">
                            <p style="margin: 0; color: #856404; font-size: 14px;">
                                ⏰ This OTP will expire in 10 minutes
                            </p>
                        </div>
                        
                        <div style="background-color: #f8d7da; border: 1px solid #f5c6cb; padding: 15px; border-radius: 5px; margin-bottom: 20px;">
                            <p style="margin: 0; color: #721c24; font-size: 14px;">
                                🔒 If you didn't request this password reset, please ignore this email or contact support.
                            </p>
                        </div>
                        
                        <p style="margin: 0; color: #666; font-size: 14px;">
                            Need help? Contact us at <a href="mailto:support@tasky.com" style="color: #6f42c1;">support@tasky.com</a>
                        </p>
                    </div>
                    
                    <!-- Footer -->
                    <div style="background-color: #f8f9fa; padding: 15px; text-align: center; border-top: 1px solid #eee;">
                        <p style="margin: 0; color: #999; font-size: 12px;">© 2026 Tasky. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, organizationName, otp);
    }

    public String buildPasswordResetSuccessTemplate(String organizationName) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f9f9f9;">
                <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    
                    <!-- Header -->
                    <div style="background-color: #28a745; padding: 30px; text-align: center;">
                        <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: normal;">
                            Password Reset Successful
                        </h1>
                    </div>
                    
                    <!-- Content -->
                    <div style="padding: 30px;">
                        <h2 style="color: #333; margin: 0 0 15px 0; font-size: 20px;">
                            Hello %s!
                        </h2>
                        
                        <p style="margin-bottom: 20px; font-size: 16px; color: #555;">
                            Your organization password has been successfully reset. You can now log in with your new password.
                        </p>
                        
                        <div style="background-color: #d4edda; border: 1px solid #c3e6cb; padding: 15px; border-radius: 5px; margin-bottom: 20px;">
                            <p style="margin: 0; color: #155724; font-size: 14px;">
                                ✓ Password reset completed successfully
                            </p>
                        </div>
                        
                        <div style="text-align: center; margin: 25px 0;">
                            <a href="#" style="background-color: #28a745; color: #ffffff; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;">
                                Login to Dashboard
                            </a>
                        </div>
                        
                        <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin-bottom: 20px;">
                            <p style="margin: 0; color: #856404; font-size: 14px;">
                                🔒 For security, please ensure you keep your new password safe and don't share it with anyone.
                            </p>
                        </div>
                        
                        <p style="margin: 0; color: #666; font-size: 14px;">
                            Questions? Contact us at <a href="mailto:support@tasky.com" style="color: #28a745;">support@tasky.com</a>
                        </p>
                    </div>
                    
                    <!-- Footer -->
                    <div style="background-color: #f8f9fa; padding: 15px; text-align: center; border-top: 1px solid #eee;">
                        <p style="margin: 0; color: #999; font-size: 12px;">© 2026 Tasky. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, organizationName);
    }
}

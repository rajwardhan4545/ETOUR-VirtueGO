import React, { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { customerAPI } from '../api';
import Card from '../components/UI/Card';
import TextInput from '../components/Forms/TextInput';

const ResetPassword = () => {
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token');
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        password: '',
        confirmPassword: ''
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);

    // Redirect if no token is present
    if (!token) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
                <div className="max-w-md w-full text-center">
                    <h2 className="text-xl font-bold text-red-600">Invalid Request</h2>
                    <p className="mt-2 text-gray-600">Missing password reset token.</p>
                    <button
                        onClick={() => navigate('/login')}
                        className="mt-4 text-blue-600 hover:underline"
                    >
                        Go to Login
                    </button>
                </div>
            </div>
        );
    }

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
        setError('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (formData.password !== formData.confirmPassword) {
            setError("Passwords don't match");
            return;
        }

        if (formData.password.length < 6) {
            setError("Password must be at least 6 characters");
            return;
        }

        setLoading(true);

        try {
            await customerAPI.resetPassword({
                token,
                newPassword: formData.password
            });
            setSuccess('Password has been reset successfully!');
            setTimeout(() => navigate('/login'), 2000);
        } catch (err) {
            setError(err.response?.data || 'Failed to reset password');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md w-full space-y-8">
                <div className="text-center">
                    <h2 className="mt-6 text-3xl font-extrabold text-gray-900">
                        Reset Password
                    </h2>
                    <p className="mt-2 text-sm text-gray-600">
                        Enter your new password below
                    </p>
                </div>

                <Card>
                    <form className="p-6 space-y-6" onSubmit={handleSubmit}>
                        {error && (
                            <div className="p-4 bg-red-50 text-red-700 rounded-md text-sm">
                                {error}
                            </div>
                        )}

                        {success && (
                            <div className="p-4 bg-green-50 text-green-700 rounded-md text-sm">
                                {success}
                            </div>
                        )}

                        <TextInput
                            label="New Password"
                            name="password"
                            type="password"
                            value={formData.password}
                            onChange={handleChange}
                            placeholder="Enter new password"
                            required
                        />

                        <TextInput
                            label="Confirm Password"
                            name="confirmPassword"
                            type="password"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            placeholder="Confirm new password"
                            required
                        />

                        <div>
                            <button
                                type="submit"
                                disabled={loading || success}
                                className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
                            >
                                {loading ? 'Processing...' : 'Reset Password'}
                            </button>
                        </div>
                    </form>
                </Card>
            </div>
        </div>
    );
};

export default ResetPassword;
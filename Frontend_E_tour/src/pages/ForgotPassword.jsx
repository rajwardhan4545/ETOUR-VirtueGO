import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { customerAPI } from '../api';
import Card from '../components/UI/Card';
import TextInput from '../components/Forms/TextInput';

const ForgotPassword = () => {
    const [email, setEmail] = useState('');
    const [status, setStatus] = useState({ type: '', message: '' });
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!email) return;

        setLoading(true);
        setStatus({ type: '', message: '' });

        try {
            await customerAPI.forgotPassword(email);
            setStatus({
                type: 'success',
                message: 'Password reset link has been sent to your email.'
            });
            setEmail('');
        } catch (error) {
            setStatus({
                type: 'error',
                message: error.response?.data || 'Failed to send reset link. Please try again.'
            });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md w-full space-y-8">
                <div className="text-center">
                    <h2 className="mt-6 text-3xl font-extrabold text-gray-900">
                        Forgot your password?
                    </h2>
                    <p className="mt-2 text-sm text-gray-600">
                        Enter your email address and we'll send you a link to reset your password.
                    </p>
                </div>

                <Card>
                    <form className="p-6 space-y-6" onSubmit={handleSubmit}>
                        {status.message && (
                            <div className={`p-4 rounded-md ${status.type === 'success' ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'
                                }`}>
                                {status.message}
                            </div>
                        )}

                        <TextInput
                            label="Email address"
                            name="email"
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="Enter your email"
                            required
                        />

                        <div>
                            <button
                                type="submit"
                                disabled={loading}
                                className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
                            >
                                {loading ? (
                                    <div className="flex items-center">
                                        <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                                        Sending...
                                    </div>
                                ) : (
                                    'Send Reset Link'
                                )}
                            </button>
                        </div>

                        <div className="text-center">
                            <Link to="/login" className="font-medium text-blue-600 hover:text-blue-500">
                                Back to Sign in
                            </Link>
                        </div>
                    </form>
                </Card>
            </div>
        </div>
    );
};

export default ForgotPassword;
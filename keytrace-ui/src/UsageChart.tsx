import { useState, useEffect } from 'react';
import axios from 'axios';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

interface UsageData {
    endpoint: string;
    totalCalls: number;
    averageLatencyMs: number;
}

interface UsageChartProps {
    keyId: string | null;
}

export default function UsageChart({ keyId }: UsageChartProps) {
    const [data, setData] = useState<UsageData[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!keyId) return;

        const fetchUsage = async () => {
            setLoading(true);
            setError('');
            try {
                const response = await axios.get(`http://localhost:8081/api/keys/${keyId}/usage`);
                setData(response.data);
            } catch (err) {
                setError('Failed to fetch usage data.');
                setData([]);
            }
            setLoading(false);
        };

        fetchUsage();
    }, [keyId]);

    if (!keyId) {
        return (
            <div className="bg-gray-800 p-6 rounded-lg mt-8">
                <h2 className="text-xl font-semibold mb-4 text-blue-400">📊 Usage Analytics</h2>
                <p className="text-gray-500 text-center py-8">
                    Click "View Usage" on any key above to see its analytics.
                </p>
            </div>
        );
    }

    return (
        <div className="bg-gray-800 p-6 rounded-lg mt-8">
            <h2 className="text-xl font-semibold mb-4 text-blue-400">📊 Usage Analytics</h2>

            {loading && <p className="text-blue-400">Loading chart...</p>}
            {error && <p className="text-red-400 mb-4">{error}</p>}

            {data.length > 0 && !loading ? (
                <div className="bg-gray-900 p-4 rounded">
                    <ResponsiveContainer width="100%" height={300}>
                        <LineChart data={data}>
                            <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                            <XAxis dataKey="endpoint" stroke="#9CA3AF" />
                            <YAxis stroke="#9CA3AF" />
                            <Tooltip
                                contentStyle={{ backgroundColor: '#1F2937', border: '1px solid #374151' }}
                            />
                            <Legend />
                            <Line type="monotone" dataKey="totalCalls" stroke="#3B82F6" strokeWidth={2} name="Total Calls" />
                            <Line type="monotone" dataKey="averageLatencyMs" stroke="#10B981" strokeWidth={2} name="Avg Latency (ms)" />
                        </LineChart>
                    </ResponsiveContainer>
                </div>
            ) : (
                !loading && !error && (
                    <p className="text-gray-500 text-center py-8">
                        No usage data yet. Hit the /api/v1/secret-data endpoint with this key a few times.
                    </p>
                )
            )}
        </div>
    );
}
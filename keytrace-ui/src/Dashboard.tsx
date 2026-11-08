import { useState } from 'react';
import axios from 'axios';
import UsageChart from './UsageChart';

interface ApiKey {
    id: string;
    name: string;
    rawApiKey: string;
    message: string;
}

export default function Dashboard() {
    const [keys, setKeys] = useState<ApiKey[]>([]);
    const [name, setName] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [selectedKeyId, setSelectedKeyId] = useState<string | null>(null);

    // Use the environment variable, fallback to localhost for local dev
    const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081';

    const createKey = async () => {
        setIsLoading(true);
        try {
            const response = await axios.post(`${API_BASE_URL}/api/keys`, {
                userId: '11111111-1111-1111-1111-111111111111',
                name: name || 'New Key',
                scopes: ['read', 'write'],
                rateLimitPerMinute: 50,
            });
            setKeys([response.data, ...keys]);
            setName('');
        } catch (error) {
            alert('Failed to create key. Is the backend running?');
        }
        setIsLoading(false);
    };

    return (
        <div className="min-h-screen bg-gray-900 text-white p-8">
            <div className="max-w-4xl mx-auto">
                <header className="mb-8">
                    <h1 className="text-4xl font-bold text-blue-400">Keytrace</h1>
                    <p className="text-gray-400 mt-2">API Key Management & Usage Dashboard</p>
                </header>

                {/* Create Key Section */}
                <div className="bg-gray-800 p-6 rounded-lg mb-8">
                    <h2 className="text-xl font-semibold mb-4">Create New API Key</h2>
                    <div className="flex gap-4">
                        <input
                            type="text"
                            placeholder="Key Name (e.g. Production)"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            className="flex-1 p-3 rounded bg-gray-700 text-white border border-gray-600 focus:outline-none focus:border-blue-500"
                        />
                        <button
                            onClick={createKey}
                            disabled={isLoading}
                            className="bg-blue-600 hover:bg-blue-700 px-6 py-3 rounded font-semibold disabled:opacity-50"
                        >
                            {isLoading ? 'Creating...' : 'Generate Key'}
                        </button>
                    </div>
                </div>

                {/* Display Keys */}
                <div className="space-y-4">
                    {keys.length === 0 && (
                        <p className="text-gray-500 text-center py-8">No keys generated yet.</p>
                    )}
                    {keys.map((key) => (
                        <div key={key.id} className="bg-gray-800 p-6 rounded-lg border-l-4 border-blue-500">
                            <div className="flex justify-between items-start">
                                <div>
                                    <h3 className="text-lg font-bold">{key.name}</h3>
                                    <p className="text-sm text-gray-300 mt-1">
                                        <span className="font-bold text-blue-300">ID:</span> {key.id}
                                    </p>
                                </div>
                                <div className="flex flex-col gap-2">
                                    <span className="bg-green-900 text-green-300 text-xs px-2 py-1 rounded text-center">Active</span>
                                    <button
                                        onClick={() => setSelectedKeyId(key.id)}
                                        className="bg-purple-600 hover:bg-purple-700 text-xs px-3 py-1 rounded font-semibold"
                                    >
                                        View Usage
                                    </button>
                                </div>
                            </div>

                            {/* Show raw key only once */}
                            <div className="mt-4 bg-gray-900 p-3 rounded border border-yellow-600">
                                <p className="text-yellow-400 text-xs mb-1 font-bold">⚠️ SAVE THIS KEY NOW. It will never be shown again.</p>
                                <code className="text-green-400 text-sm break-all">{key.rawApiKey}</code>
                            </div>
                        </div>
                    ))}
                </div>

                {/* Pass the selected Key ID to the Chart */}
                <UsageChart keyId={selectedKeyId} />
            </div>
        </div>
    );
}
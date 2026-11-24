// Recharts BarBreakdown component per Item 46

import React from 'react';
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
} from 'recharts';

interface BarBreakdownProps {
  data: Array<{ name: string; [key: string]: any }>;
  bars: Array<{ key: string; color: string; name: string }>;
  height?: number;
}

export const BarBreakdown: React.FC<BarBreakdownProps> = ({ data, bars, height = 280 }) => {
  return (
    <div style={{ width: '100%', height }}>
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={data} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
          <XAxis dataKey="name" stroke="#64748b" fontSize={12} tickLine={false} />
          <YAxis stroke="#64748b" fontSize={12} tickLine={false} />
          <Tooltip
            contentStyle={{ backgroundColor: '#ffffff', borderRadius: '6px', border: '1px solid #cbd5e1' }}
          />
          <Legend wrapperStyle={{ fontSize: '12px' }} />
          {bars.map((b) => (
            <Bar key={b.key} dataKey={b.key} name={b.name} fill={b.color} radius={[4, 4, 0, 0]} />
          ))}
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

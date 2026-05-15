import type { Config } from 'tailwindcss'

const config: Config = {
  content: [
    './pages/**/*.{js,ts,jsx,tsx,mdx}',
    './components/**/*.{js,ts,jsx,tsx,mdx}',
    './app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        background: '#121212',
        surface: '#1E1E1E',
        'surface-2': '#2A2A2A',
        'vialgo-green': '#2E7D32',
        'vialgo-green-light': '#4CAF50',
        'vialgo-amber': '#F9A825',
        'vialgo-amber-light': '#FFCA28',
      },
    },
  },
  plugins: [],
}

export default config

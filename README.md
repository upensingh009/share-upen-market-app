# share-upen-market-app

This project is a local-only Android prototype for a personal trading assistant that:
- skips login and auto-authenticates with locally stored Angel One credentials
- watches market prices for NIFTY and SENSEX
- uses a simple local signal engine for trade suggestions
- emits a simple in-app decision message for buy/sell/hold actions

## Structure
- auth: encrypted local credential storage and auth helper
- market: market data access and WebSocket placeholder logic
- suggestion: RSI/MACD/MA-based heuristics
- trading: trade decision engine

## Notes
- This is not for publishing and is intended only for personal/local experimentation.
- Replace the demo credential values in the encrypted store with your own local values.
- A real Angel One integration would need the official SmartAPI client and a valid API key/client credentials.

## Build locally
1. Install Android Studio and the Android SDK.
2. Open the project in Android Studio.
3. Sync Gradle and run the app on an emulator or device.

## Expected behavior
On launch, the app will:
1. load securely stored local credentials
2. authenticate locally with a demo token path
3. fetch a simulated live price
4. produce a simple buy/sell/hold recommendation

# Application Blueprint: Institutional Finance Protocol

A production-grade personal finance ecosystem focusing on data density, high-end "institutional" aesthetics, and strategic capital management.

## 1. System Architecture

### Core Tech Stack
- **Visuals:** Recharts (Deployment velocity and asset distribution)
- **Animation:** Motion/React (Fluid transitions and hover state biology)
- **Icons:** Lucide React (Tactical UI indicators)

## 2. Information Architecture (Tabs)

### 1. Pulse (Dashboard)
- **Health Protocol:** Real-time calculation of "System Health" based on budget adherence.
- **KPI Grid:** Quick-read cards for balance, upcoming obligations, and active aspirations.
- **Tip Engine:** Daily algorithmic insights for capital optimization.

### 2. Ledger (Expenses)
- **High-Velocity Search:** Instant filtering by merchant or category.
- **Asset Type Filtering:** Segmented control for quick categorization.
- **Ledger Entries:** Grouped by date (Today/Yesterday) for chronological clarity.

### 3. Strategy (Planning Hub)
*Redesigned consolidated hub for resource planning.*
- **Protocol (Budget):** Setting hard limits on category-specific deployment.
- **Obligations (Bills):** Managing recurring billing cycles and subscription decay.
- **Aspirations (Goals):** Long-term capital accumulation targets with progress mapping.

### 4. Analysis (Reports)
- **Deployment Velocity:** Area charts showing spending trends over time.
- **Asset Distribution:** Pie charts mapping capital concentration.
- **Efficiency Ratings:** Progress bars for savings velocity and budget adherence.

## 3. Design Philosophy

### The "Institutional" Aesthetic
- **Typography:** Inter (Sans) for data nodes, Space Grotesk (Display) for headers.
- **Color Palette:**
  - `Slate-900`: Principal background and primary text.
  - `Blue-600`: Interactive accents and "Protocol" signifiers.
  - `Emerald-500`: "Stable" and "Growth" indicators.
- **Geometry:** Large corner radii (`rounded-[3rem]`) to soften technical density.
- **Currency:** Normalized for Naira (₦) with custom local formatting.

## 4. Data Schemas

### Expense Node
```typescript
{
  id: string;
  amount: number;
  category: 'Food' | 'Transport' | 'Shopping' | 'Entertainment' | 'Health' | 'Bills' | 'Other';
  date: string;
  notes: string;
}
```

### Profile Configuration
```typescript
{
  name: string;
  monthlyIncome: number;
  payday: number;
  currency: 'NGN' | 'USD' | 'EUR';
  privacyMode: boolean; // Blurs sensitive digits
  lowBalanceThreshold: number;
}
```

## 5. Unique UX Interactions
- **Privacy Toggle:** Central shift to blur all currency nodes for public environment usage.
- **Tactical Calculator:** Floating math engine positioned for quick balance checks (Fixed bottom-44 avoidances).
- **Detail Chips:** Predictive note entry for one-tap merchant logging.

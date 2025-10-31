# Product Catalog — Detailed UI Specification for Java Swing Implementation

Below is a detailed, implementation-ready description of the interface in the screenshot you provided. It covers layout, sizes, colors (HEX), fonts, component types, behaviors, accessibility and styling tips (rounded panels, shadows, custom buttons). Use it directly to implement with Swing (`JFrame`, `JPanel`, `JTable`, `JButton`, `JTextField`, `JComboBox`, `JRadioButton`, `JCheckBox`, `JDatePicker/JCalendar`, custom painting where needed).

---

## Overall window / frame
- **Frame size (suggested):** `1000 × 520` px (or scalable; aspect ratio same).
- **Layout:** `BorderLayout` for outer frame; inner content uses nested `JPanel`s with `GridBagLayout` (or `GroupLayout`) for precise placement.
- **Background color (page):** deep navy — `#071833`.
- **Outer content margin:** 12–18 px padding around edges.

---

## Top bar (title + small icon)
- **Small shopping-cart icon** at top-left (approx `24 × 24` px), followed by label:
- **Title text:** `Product catalog`
  - Font: `Segoe UI` / `Tahoma` / `SansSerif`
  - Size: `16–18pt`, weight **bold**
  - Color: `#E9F6FF` (very pale blue)
  - Left padding from window: ~10 px

---

## Main content split
The main content divides horizontally into:

1. **Left: big rounded "Product data" panel + Search area and table below**
2. **Right: vertical stack of tall rounded action buttons (Add, Update, Delete, Consult)**

Use a parent `JPanel` with `BorderLayout` or a `GridBagLayout` with two columns: left column flexible width, right column fixed width ~150 px for buttons.

---

## Product Data Panel (top-left)
- **Container:** rounded rectangle panel with soft border and small drop-shadow.
  - Size: full left-column width, height ~180 px.
  - Background: subtle vertical gradient — top `#DDF5FF` → bottom `#C7EFFF`.
  - Inner panel border (stroke): `#8FC7FF`, border thickness 2 px.
  - Corner radius: `16–20` px.
- **Title (inside panel):** `Product data` (upper-left of panel)
  - Font: `Segoe UI`, `italic bold`, `14pt`
  - Color: `#F8FFFF` or `#ECF8FF`
- **Padding inside panel:** 12–16 px.

### Fields & layout (use `GridBagLayout` or `GroupLayout`)
Place a two-row, multi-column form roughly like the screenshot:

Row 1 (left to right)
- **Label "ID:"** `JLabel` (12pt, bold, color `#0B2C4A` or dark blue)
- **ID field:** `JTextField`, preferred size `120 × 26` px, `editable=false` if auto-generated.
- **Label "Description:"**
- **Description field:** `JTextField`, preferred width `460 × 26` px

Row 2
- **Label "Brand:"**
- **Brand selector:** `JComboBox<String>` (with small blue dropdown arrow icon), preferred `160 × 26`.
- **Label "Content:"**
- **Content field:** `JTextField`, width `280 × 26`.
- **Label "Price:"**
- **Price field:** `JFormattedTextField` (for currency), width `80 × 26`.

Row 3
- **Label "Status:"**
  - `JCheckBox` labeled `Active` (selected color: blue fill).
- **Label "Category:"**
  - A rounded inner panel (pill) containing **radio buttons** for categories:
    - `JRadioButton` in a `ButtonGroup`: `Groceries`, `Personal hygiene`, `Fruits and vegetables`, `Wines and spirits`.
    - Arrange horizontally with spacing; each radio with custom circular marker (fill blue when selected).
    - Category panel background: `#FFFFFF` or `#F5FBFF` with border `#A7DBFF` and corner radius 12 px.

Row 4
- **Label "Date made:"**
  - `JDatePicker` or `JTextField` with calendar icon (use JCalendar library)
  - Field width `150 × 26`.

- **Label "Expiration date:"**
  - `JDatePicker` width `150 × 26`.

**Field label style**
- Font: `Segoe UI` / `Tahoma`, `12pt`, **bold**.
- Color for labels: `#072B4A` (dark desaturated navy/blue).
- Spacing between label and field: 8–12 px.

**Field style**
- Background: `#F2FBFF` (very light baby-blue) for `JTextField` and `JComboBox`.
- Border: rounded rectangle 6–8 px radius with inner border color `#BFE8FF`.
- Insets/padding inside fields: 6–8 px.
- Placeholder / tooltip: example `Enter product description...`.

---

## Action Buttons (right column)
Vertical stack: four large rounded buttons with icon circle + text aligned to the right of the icon.

- **Panel width:** ~150 px; top margin align with Product data panel.
- **Each button shape:** rounded rectangle with a circular icon on left (black circle with white plus). Button overall size: `140 × 80` px; icon circle `48 × 48` centered vertically.
- **Button background:** dark navy `#041827` or `#0A1B2E`.
- **Button border:** 2 px pale blue `#CFE7FF`.
- **Icon circle:** center-fill black `#000000` with white plus `#FFFFFF`. Outer ring (thin) in pale blue `#EAF6FF` to match screenshot.
- **Button label (right of icon):** e.g., `Add`, `Update`, `Delete`, `Consult`
  - Font: `Segoe UI`, `14pt`, **bold**
  - Color: `#EAF6FF` (pale)
  - Align text vertically centered, left aligned to icon.
- **Hover effect:** slightly lighter background or outer glow.
- **Click effect:** brief darker pressed color.
- **Accessibility:** set mnemonics — Add `Alt+A`, Update `Alt+U`, Delete `Del` (or Alt+D), Consult `Alt+C`. Also provide tooltips.

**Implementation notes**
- Use `JButton` with `setBorderPainted(false)` and custom `paintComponent` for rounded corners, or create a reusable `RoundedButton` class.
- Icon: use `ImageIcon` or draw on `BufferedImage`.

---

## Search data area (below product data panel)
- **Label:** `Search data` (14pt, italic/bold, color `#EAF6FF`)
- **Search bar container:** rounded rectangle panel with same style as product data panel but smaller height.
  - Padding: 8 px.
  - Components inside:
    - **Left:** small `JComboBox` filter (for search type), rounded.
    - **Center:** `JTextField` wide for query, preferred full width.
    - **Right:** two circular buttons: **Search** (magnifying glass) and **Refresh** (circular arrow)
      - Buttons are small, circular, white background with dark icon stroke or vice versa.
      - Size: `36 × 36` px each with 8 px gap.

**Styling**
- Search field background: `#FFFFFF` or `#F7FBFF`.
- Border: `#BFE8FF`, radius 8 px.
- Placeholder text: `Search by description, brand, id...`

---

## Results Table
- **Component:** `JTable` inside a rounded `JPanel` with light blue header band.
- **Header background:** pale sky-blue `#BFE7FF`.
  - Header font: `Segoe UI`, `12pt`, **bold**, color `#07324D`.
- **Row background:** alternating white `#FFFFFF` and very light blue `#F9FDFF` (optional).
- **Grid lines:** `#DDEEF8` (very light).
- **Table selection:** `ListSelectionModel.SINGLE_SELECTION`.
- **Auto-resize mode:** `AUTO_RESIZE_OFF` and specify preferred column widths.
- **Columns** (order and suggested widths):
  - `ID` — 50 px (center)
  - `Description` — 300 px
  - `Brand` — 120 px
  - `Content` — 120 px
  - `Price` — 80 px (right aligned)
  - `Category` — 150 px
  - `Status` — 80 px (center)
  - `Date made` — 120 px
  - `Expiration date` — 120 px
- **Row height:** `28–30` px.
- **Empty rows in screenshot:** show placeholder rows 1..4 — implement by filling table model with sample or use `DefaultTableModel`.
- **Vertical scrollbar:** narrow, custom thumb color `#2B6FAF` on track `#F2F8FF`.
- **Corner radius and border of table container:** 12 px, border color `#A7D7FF`.

**Table model**
- Use `AbstractTableModel` or `DefaultTableModel` backed by `List<Product>`.
- Provide cell renderers:
  - Price: `NumberFormat.getCurrencyInstance()` renderer, right aligned.
  - Date columns: formatted as `dd/MM/yyyy`.
  - Status: show `Active` as a green dot/label or checkbox (non-editable).
  - Category: plain string.

---

## Fonts (summary)
- Title: `Segoe UI`, `18pt`, **bold**
- Section titles (Product data / Search data): `Segoe UI`, `14pt`, **bold italic**
- Field labels: `Segoe UI`, `12pt`, **bold**
- Input text: `Segoe UI`, `12pt`, plain
- Button text: `Segoe UI`, `14pt`, **bold**
- Table header: `Segoe UI`, `12pt`, **bold**
- Table body: `Segoe UI`, `11–12pt` plain

Fallback fonts: `Tahoma` or `SansSerif`.

---

## Colors (exact suggestions / palette)
- **Page background (navy):** `#071833`
- **Panel outer frame border (pale):** `#C7E6FF`
- **Product panel gradient (top → bottom):** `#DDF5FF` → `#C7EFFF`
- **Panel inner border:** `#8FC7FF`
- **Field background:** `#F2FBFF`
- **Search field background:** `#FFFFFF`
- **Table header:** `#BFE7FF`
- **Table row alt:** `#F9FDFF`
- **Action button background:** `#0A1B2E`
- **Action button border:** `#CFE7FF`
- **Icon circle (button):** `#000000` (with white plus)
- **Labels (dark):** `#072B4A`
- **Primary accent / radio selected fill:** `#2D85FF` (vibrant blue)
- **Text on dark backgrounds:** `#EAF6FF`

---

## Visual details & effects
- **Rounded corners:** use radii between `8 – 20 px` according to element (larger for big panels).
- **Subtle shadows:** (optional) `DropShadow` for product panel and action buttons; implement by painting a translucent rounded rectangle offset by 2–4 px.
- **Radio buttons:** custom circular markers filled with `#2D85FF` when selected; no square boxes.
- **Icons:** use vector SVG or PNG at multiple sizes:
  - Cart icon: `24 × 24`
  - Calendar icon inside date fields: `18 × 18`
  - Plus icon inside black circle: `32–36` px plus thickness
  - Magnifier icon: `18–20` px
  - Refresh icon: `18–20` px
- **Tooltips:** all actionable controls should have helpful tooltips. E.g., `Add: Add a new product (Ctrl+N)`.

---

## Accessibility & Keyboard shortcuts
- **Mnemonics:** Alt + letter for fields/buttons where appropriate.
- **Keyboard shortcuts:**
  - `Ctrl+N` — Add
  - `Ctrl+U` — Update
  - `Delete` — Delete selected row
  - `Ctrl+F` — Focus search
  - `Enter` in search field — perform search
- **Focus traversal:** logical order left-to-right, top-to-bottom.
- **High-contrast mode:** ensure text color contrast on dark backgrounds is >4.5:1.

---

## Recommended Swing components & libraries
- **Core Swing:** `JFrame`, `JPanel`, `JLabel`, `JTextField`, `JFormattedTextField`, `JButton`, `JTable`, `JScrollPane`, `JComboBox`, `JCheckBox`, `JRadioButton`, `ButtonGroup`.
- **Date picker:** use **JCalendar** or **JDatePicker** (you already mentioned jcalendar-1.4 in previous messages) — integrate `JDateChooser` for `Date made` and `Expiration date`.
- **Custom drawing:** make reusable `RoundedPanel` and `RoundedButton`. Override `paintComponent(Graphics g)` and use `Graphics2D` with `RenderingHints` to draw `fillRoundRect` + borders + optional shadow.
- **Icons:** keep in `/resources/icons/` and use `ImageIcon`. For crispness, use vector SVG and render to `BufferedImage` at multiple scales if possible.
- **Table model:** custom `AbstractTableModel` backed by domain `Product` objects.

---

## Interaction and behavior details
- **Add button:** opens a form or uses the same Product Data fields; Validate required fields (Description, Brand, Category). On success, append row to table and clear input fields (ID auto-increment).
- **Update:** only enabled if a row is selected. Populates Product Data fields with selected row data. Save writes to DB and refreshes table.
- **Delete:** confirm modal `JOptionPane.showConfirmDialog` before deletion. Disabled if no selection.
- **Consult:** show read-only details dialog or highlight row.
- **Search:** quick search live on `DocumentListener` or on pressing Search button; filter table model.
- **Refresh (circular icon):** reload table from DB.
- **Status checkbox:** toggles product active/inactive. Show inactive rows grayed text or with `Inactive` badge.
- **Validation:** price must be numeric and positive; dates: `Expiration >= Date made` if set; brand must be selected.

---

## Implementation snippets & hints (conceptual)
- Create a `RoundedPanel` class to paint background gradient and border.
- Create a `RoundedButton` with embedded icon circle drawn in `paintComponent`, center the plus icon using `Font` or `Graphics2D` stroke.
- Use `GridBagLayout` for the form for best control across different sizes.
- Use `TableCellRenderer` for price and date formats and for coloring `Status`.

---

## Example component hierarchy (logical)
```
JFrame (BorderLayout)
 ├─ JPanel(main, padding) (GridBagLayout: 2 cols)
 │   ├─ JPanel(leftColumn) (BorderLayout / BoxLayout Y_AXIS)
 │   │    ├─ RoundedPanel(productDataPanel) (GridBagLayout)  <- form fields
 │   │    └─ RoundedPanel(searchPanel) (BorderLayout)
 │   │         ├─ search controls (combo, text, search/refresh buttons)
 │   │         └─ JScrollPane -> JTable (results)
 │   └─ JPanel(rightColumn) (BoxLayout Y_AXIS)
 │        ├─ RoundedButton(Add)
 │        ├─ RoundedButton(Update)
 │        ├─ RoundedButton(Delete)
 │        └─ RoundedButton(Consult)
```
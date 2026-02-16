front-end # Si tiene una 👌= implementado en el proyecto.
├
├─ src
│
│  ├─ app                     # Bootstrap de la app
│  │  ├─ providers            # Theme, QueryClient, etc.
│  │  └─ routes
│  │     └─ AppRouter.tsx     #👌
│
│  ├─ modules                 # Features (producto real)
│  │  ├─ landing
│  │  │  ├─ pages
│  │  │  │  └─ LandingPage.tsx #👌
│  │  │  └─ components
│  │  │     └─ HeroSection.tsx  # 📋Por agregar
│  │  │
│  │  ├─ upload
│  │  │  ├─ pages
│  │  │  │  └─ UploadPage.tsx  # 👌
│  │  │  ├─ components
│  │  │  │  └─ UploadDropzone.tsx # 📋 por agregar
│  │  │  └─ application
│  │  │     └─ uploadVideo.usecase.ts # 📋 por agregar
│  │  │
│  │  ├─ preview
│  │  │  ├─ pages #👌
│  │  │  │  └─ PreviewPage.tsx
│  │  │  └─ components # 👌
│  │  │     └─ VideoPreviewPlayer.tsx
│  │  │
│  │  └─ success
│  │     └─ pages 👌
│  │        └─ SuccessPage.tsx  #📋
│
│  ├─ domain                  # Núcleo puro
│  │  ├─ entities
│  │  ├─ value-objects
│  │  ├─ policies
│  │  └─ types
│
│  ├─ application             # Lógica de aplicación transversal
│  │  ├─ use-cases
│  │  ├─ ports
│  │  ├─ state
│  │  └─ store
│
│  ├─ ui                      # UI compartida (no features)
│  │  ├─ layout
│  │  │  ├─ AppLayout.tsx
│  │  │  └─ AppHeader.tsx
│  │  ├─ components
│  │  │  └─ Logo.tsx
│  │  └─ theme
│  │     └─ ThemeToggle.tsx
│
│  ├─ components              # Design system (shadcn) # 👌
│  │  └─ ui
│  │     ├─ button.tsx
│  │     ├─ card.tsx
│  │     └─ ...
│
│  ├─ infrastructure          # Implementaciones técnicas
│  │  ├─ api 👌
│  │  │  └─ videoApi.ts
│  │  ├─ adapters 👌
│  │  │  └─ uploadAdapter.ts
│  │  └─ styles
│
│  ├─ lib
│  │  └─ utils.ts
│
│  ├─ index.css
│  ├─ App.tsx
│  └─ main.tsx
│
├─ tailwind.config.js
├─ tsconfig.app.json
├─ tsconfig.json
├─ tsconfig.node.json
└─ vite.config.ts



```
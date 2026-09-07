package com.example.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DatabaseSeeder {

    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun getSeedDigestItems(): List<DigestItem> {
        val today = getTodayDateString()
        return listOf(
            // --- 5 DEEP DIVES (isDailyPulse = false) ---
            DigestItem(
                category = Category.Polity,
                headline = "Parliament Introduces Draft Bill for Decentralized Governance",
                previewText = "A landmark bill aims to increase fiscal autonomy for urban local bodies, allowing them to directly levy municipal bonds.",
                contextText = "The Ministry of Urban Affairs introduced the Municipal Decentralization Bill in the lower house today. It aims to restructure the revenue pathways of municipal corporations to make them self-sustaining units.",
                keyPointsText = "1. Financial autonomy is granted to municipalities with populations exceeding 1 million.\n2. Standardized municipal bond frameworks to ease secondary market trading.\n3. Direct accountability measures with public disclosure of city credit scores annually.\n4. Creation of a federal municipal finance council.",
                whyItMattersText = "Urban public infrastructure is heavily underfunded, with a 70% reliance on state grants. This bill empowers cities to raise capital directly, accelerating smart city developments and climate-resilient grids.",
                examAngleText = "Expect questions on local self-government structures (74th Constitutional Amendment), urban municipal bonds, and fiscal federalism indicators in competitive public service exams.",
                sourceAFraming = "The Daily Times (Center-Left): Applauds the democratic devolution of powers, emphasizing the positive impact of local community participation on budget control and city maintenance.",
                sourceBFraming = "Financial Observer (Fiscal-Conservative): Supports the fiscal accountability aspect, but raises warnings that financially weak municipalities might default on bonds, risking local taxpayer bailouts.",
                sourceCFraming = "The State Chronicle (Regionalist): Criticizes the federal municipal council as an overreach of central authority that bypasses state government jurisdiction over local governance.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = false
            ),
            DigestItem(
                category = Category.Economy,
                headline = "Central Bank Pauses Rates Citing Structural Supply-Side Easing",
                previewText = "The monetary policy committee voted unanimously to maintain the benchmark repo rate at 6.25% as commodity price indices cool down.",
                contextText = "Following six consecutive quarters of hikes, the central bank opted for a pause today. Softening fuel and food import costs have allowed retail inflation to retreat within the target comfort band of 2% to 4%.",
                keyPointsText = "1. Repo rate remains steady at 6.25%.\n2. Reverse repo rate adjusted to 3.75% to absorb short-term systemic liquidity.\n3. GDP growth projection for the current fiscal year remains healthy at 6.8%.\n4. Retail inflation decelerated to 4.1% year-on-year.",
                whyItMattersText = "For borrowers, this signals a plateauing of interest rates on home and education loans. For investors, it indicates a stable bond yield curve, enabling better long-term capital forecasting across industrial projects.",
                examAngleText = "Syllabus correlation: Monetary policy tools (Repo, Reverse Repo, CRR, SLR), inflation index measurements (CPI vs. WPI), and the mechanics of core inflation vs. headline inflation.",
                sourceAFraming = "The Globe & Mail (Pro-Business): Celebrates the rate pause as a catalyst for immediate corporate investment, unlocking stalled capital expenditure plans in real estate and manufacturing.",
                sourceBFraming = "Labor Sentinel (Socialist): Points out that while interest rates are pausing, real wages remain stagnant. High retail prices of staples continue to squeeze low-income household consumption budgets.",
                sourceCFraming = "Macro-Analytica (Academic-Neutral): Provides a balanced analysis, arguing that core inflation remains sticky and that a premature pause could backfire if energy costs spike in winter.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = false
            ),
            DigestItem(
                category = Category.Environment,
                headline = "National Wetland Initiative Targets 50 Critical Estuaries for Reclamation",
                previewText = "An emergency allocation of $500 million was announced to restore degraded mangrove biomes and build natural storm-surge buffers.",
                contextText = "Increasing storm frequencies have prompted the Ministry of Environment to launch a large-scale coastal reclamation program. The initiative will re-establish endemic mangrove varieties along heavily eroded delta networks.",
                keyPointsText = "1. Mapping of 50 major estuaries using multispectral satellite telemetry.\n2. Removal of unauthorized commercial shrimp farms within state-protected mangrove forests.\n3. Community-led nurseries where coastal residents receive stipends for planting activities.\n4. Inclusion of local fishers in bio-monitoring networks.",
                whyItMattersText = "Healthy estuaries act as critical carbon sinks, locking carbon up to ten times faster than mature inland forests. They also protect coastal fishing economies from devastating tropical cyclones.",
                examAngleText = "Focus on the Ramsar Convention on Wetlands, major mangrove bio-zones (Sundarbans, Florida Bay), coastal zone regulation (CRZ) rules, and blue carbon accounting systems.",
                sourceAFraming = "Green Horizons (Eco-Centric): Commends the initiative as an eco-first victory that respects local biosphere networks and places conservation over short-term coastal commercial development.",
                sourceBFraming = "State Development Herald (Growth-First): Expresses concerns that removing commercial aquaculture farms will hurt local seafood exports, which are critical for earning foreign currency.",
                sourceCFraming = "The Scientific Native (Community-Based): Stresses that long-term success hinges on local indigenous rights, advising that unless fishing cooperative groups are co-owners, policing will fail.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = false
            ),
            DigestItem(
                category = Category.InternationalRelations,
                headline = "Trilateral Maritime Alliance Formed to Secure Key Shipping Straits",
                previewText = "Three major regional democracies signed a joint patrol agreement to protect global supply lanes from rising maritime piracy.",
                contextText = "Amid escalating cargo insurance premiums, a trilateral maritime pact was forged today. The naval treaty establishes real-time intelligence networks and coordinated anti-piracy exercises in the southern straits.",
                keyPointsText = "1. Dedicated naval task forces for joint deep-sea escorts.\n2. Mutual defense logistics, permitting ships to refuel and repair at any allied port.\n3. Joint satellite surveillance hub covering critical bottleneck straits.\n4. Unified protocols for responding to non-state maritime threats.",
                whyItMattersText = "Over 40% of global industrial trade transits through these precise corridors. Unsecured straits directly result in longer shipping routes, driving up fuel emissions and shelf-life costs for retail goods globally.",
                examAngleText = "Study maritime choke points (Strait of Malacca, Bab-el-Mandeb, Ormuz), United Nations Convention on the Law of the Sea (UNCLOS), and bilateral defense frameworks.",
                sourceAFraming = "Security Quarterly (Strategic-Realist): Hails the alliance as a long-overdue deterrent that restores balance of power and provides necessary safety nets for commercial cargo vessels.",
                sourceBFraming = "Pacific Critic (Pacifist-Left): Warns that joint patrols are highly provocative, risking naval confrontations and escalatory arms racing in international water pathways.",
                sourceCFraming = "Global Logistics (Commerce-Focused): Remains neutral, focusing solely on shipping economics, noting that insurance rates have dropped 5% in anticipation of the joint security patrols.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = false
            ),
            DigestItem(
                category = Category.Science,
                headline = "Solid-State Quantum Engine Achieves Supercooled Stability Record",
                previewText = "Physicists maintained a stable 256-qubit coherence state for over four minutes at slightly warmer sub-Kelvin conditions.",
                contextText = "In a breakthrough research publication today, scientists announced a thermal barrier triumph. By utilizing a novel diamond nitrogen-vacancy grid, qubits remained coherent without liquid helium refrigeration.",
                keyPointsText = "1. Breakthrough 256-qubit array achieved stable operations.\n2. Stable temperature achieved at 0.5 Kelvin, avoiding expensive ultra-cool environments.\n3. Coherence lasted 245 seconds, a 10x improvement over previous benchmarks.\n4. Scalable manufacturing process utilizing industrial silicon substrates.",
                whyItMattersText = "Standard quantum computers require massive cryogenic systems, limiting them to cloud data centers. Stable operation at warmer temperatures paves the way for modular, mobile quantum units usable in cryptography and drug discovery.",
                examAngleText = "Important concepts: Quantum coherence, quantum entanglement, cryogenics, superconductors, and the difference between classic silicon bits and qubits.",
                sourceAFraming = "Future Tech Journal (Utopian): Portrays this as the immediate dawn of the quantum age, predicting desktop quantum nodes that will revolutionize secure communication in five years.",
                sourceBFraming = "The Pragmatist (Skeptical-Scientific): Cautions that while the thermal result is impressive, error-correction rates remain too high for actual commercial computing computations.",
                sourceCFraming = "The Cyber Shield (Security-Focused): Expresses deep concern over the immediate cryptographic threat, advising that defense departments must rapidly deploy quantum-resistant algorithms.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = false
            ),

            // --- 15 DAILY PULSE (isDailyPulse = true) ---
            DigestItem(
                category = Category.Polity,
                headline = "Electoral Commission Announces Direct Voting Audits Using Biometric Logs",
                previewText = "The Electoral Commission is introducing a smart check across random voting booths to keep elections super secure. They are matching physical signatures with quick biometric scans to make sure every vote is perfectly counted.",
                contextText = "The central electoral authority updated the national voting handbook today. In all future regional and national contests, physical voter sign-ins will be cross-verified against real-time biometric database logs.",
                keyPointsText = "1. Direct automated audits implemented across random booths.\n2. Cryptographically sealed logs transmitted to secure state repositories.\n3. Citizen monitoring groups invited to witness the biometric audit processes.\n4. Legal penalties doubled for election officials who fail audit protocol steps.",
                whyItMattersText = "In an era of rising institutional skepticism, clear and verifiable auditing systems prevent conspiracy theories, ensuring smooth transitions of power and preserving democratic stability.",
                examAngleText = "Be prepared to write about representative democracy institutions, electoral systems, data privacy considerations with biometric logging, and regulatory reform bills.",
                sourceAFraming = "Democracy Watch (Reformist): Celebrates this as a modern standard of transparency, ensuring election integrity while rebuilding voter trust in public bodies.",
                sourceBFraming = "Citizen Rights Advocate (Privacy-Focused): Heavily cautions against centralized storage of real-time biometric tracking logs, fearing potential police surveillance abuses.",
                sourceCFraming = "Political Analyst (Institutionalist): Points out the logistical challenges, arguing that rural voting stations with unstable power grids will face severe delays under biometric auditing.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = true
            ),
            DigestItem(
                category = Category.Economy,
                headline = "E-Commerce Rules Restrict Self-Brand Promotion on Main Portals",
                previewText = "Large shopping websites can no longer push their own private brands over local sellers in search results. This cool new rule ensures that small family businesses get a fair chance to sell their unique products.",
                contextText = "The Fair Trade Commission released a strict 40-page regulatory guide today. Designed to level the playing field, the rules prevent tech platforms from ranking their self-owned labels higher in consumer search results.",
                keyPointsText = "1. Strict separation of platform operations and retail sales.\n2. Ban on search algorithm biases favoring self-branded products.\n3. Heavy fines scaling up to 10% of global turnover for violation repeaters.\n4. Easy conflict-resolution pathways for small merchant sellers.",
                whyItMattersText = "This gives small and medium businesses (SMBs) a fighting chance on large retail portals, preventing digital monopolies from eating up local trade margins.",
                examAngleText = "Analyze anti-trust laws, vertical integration challenges in digital markets, regulatory bodies, and consumer protection policies.",
                sourceAFraming = "SME Advocate (Retail-First): Strongly endorses the rules, calling it a historic day that breaks the stranglehold of massive online monopolies over local creators.",
                sourceBFraming = "Silicon Review (Big-Tech-Friendly): Warns that restricting self-brands will drive up consumer costs, as house brands are often much cheaper than branded equivalents.",
                sourceCFraming = "The Consumer Shield (Pro-Consumer): Supports the transparency aspect, but notes that enforcement will be near-impossible without full auditing of complex proprietary search algorithms.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = true
            ),
            DigestItem(
                category = Category.Environment,
                headline = "Global Soil Health Accord Promotes Regenerative Farming",
                previewText = "Over forty countries just signed a massive agreement to restore the health of our earth's topsoil. Farmers will be rewarded for planting cover crops and keeping the soil naturally rich without chemical fertilizers.",
                contextText = "The World Soil Forum concluded today with a historic treaty signing. Participating countries will phase out broad-spectrum chemical fertilizers in favor of organic carbon-sequestering cover crops.",
                keyPointsText = "1. Mandated goal of 3% soil organic carbon content by 2040.\n2. Dynamic subsidy restructuring, rewarding farmers based on measurable soil health improvement.\n3. Gradual ban on aggressive tilling techniques on slope terrains.\n4. Creation of a global soil observatory.",
                whyItMattersText = "Degraded topsoil leads to nutrient-deficient crops and increases desertification. Restoring soil health enables secure food outputs for growing populations and traps immense amounts of greenhouse gases.",
                examAngleText = "Key topics: Soil profiles, organic matter cycles, carbon sequestration, sustainable agriculture systems, and the UN Convention to Combat Desertification (UNCCD).",
                sourceAFraming = "The Organic Earth (Permaculture-Focused): Celebrates the treaty as a seismic shift, signaling the end of toxic industrial chemical farming and the rebirth of nature-aligned agriculture.",
                sourceBFraming = "Agribusiness Daily (Corporate-Growth): Warns that a rapid transition away from modern chemical fertilizers will severely reduce grain yields, causing sudden food price inflation.",
                sourceCFraming = "The Soil Physicist (Scientific-Skeptical): Expresses technical doubts, asserting that measuring soil organic carbon at a national scale requires expensive sensors that developing countries cannot afford.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = true
            ),
            DigestItem(
                category = Category.Economy,
                headline = "Sovereign Gold Bonds Return with Simple Dynamic Rates",
                previewText = "You can now buy gold from the government without the hassle of storing physical bars. The new gold bonds come with a stable interest rate and are completely tax-free upon maturity.",
                contextText = "The Ministry of Finance in coordination with the Central Bank has launched Series IV of Sovereign Gold Bonds. The new pricing structure is designed to minimize risk from sudden gold market spikes.",
                keyPointsText = "1. Interest rate fixed at 2.50% per annum paid semi-annually.\n2. Tenor of 8 years with exit option after the 5th year.\n3. Collateral usage eligible for securing commercial credit lines.\n4. Capital gains tax exempt on redemption.",
                whyItMattersText = "SGBs reduce physical gold import demand, strengthening the national balance of trade and reducing currency depreciation pressures.",
                examAngleText = "Learn about Capital Account Convertibility, Balance of Payments (BoP), Sovereign Debt structures, and physical vs paper gold assets.",
                sourceAFraming = "Gold Bullion Review (Pro-Bullion): Strongly welcomes the bonds as a secure alternative to holding physical metal without storage or security risks.",
                sourceBFraming = "Fiscal Critic (Conservative): Cautions that the government is taking on heavy long-term commodity liabilities if gold prices skyrocket.",
                sourceCFraming = "Independent Economist (Neutral): Argues that while SGBs are highly attractive to retail savers, they represent a costly borrowing option for the state.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = true
            ),
            DigestItem(
                category = Category.Polity,
                headline = "New Central Civil Service Exam Proposed for Lower Courts",
                previewText = "A new central civil exam is being launched to hire judges and fill thousands of empty slots in local courts. This will help clear the massive backlog of pending cases and bring fresh legal talent.",
                contextText = "The Law Commission submitted a draft report recommending a standardized AIJS exam. The goal is to address the severe backlog of over 40 million pending cases nationwide.",
                keyPointsText = "1. Nationalized selection of district and sessions judges.\n2. Standardized salaries and training infrastructure.\n3. Local language proficiency requirements for postings.\n4. Multi-tier exam pattern focusing on procedural law.",
                whyItMattersText = "Currently, individual high courts hold separate recruitment, causing delays. AIJS aims to attract fresh legal talent and standardize judicial standards.",
                examAngleText = "Important concepts: Constitutional Provisions for Judiciary (Articles 233-237), federalism debates on appointments, and judicial reform metrics.",
                sourceAFraming = "The Bar Gazette (Reformist): Supports the move to professionalize lower courts, attracting high-caliber candidates who would otherwise go to corporate firms.",
                sourceBFraming = "Federation Weekly (State Rights): Criticizes the proposal as an encroachment on states' power over local administration of justice.",
                sourceCFraming = "Legal Scholar (Academic): Points out that unless local language integration is flawless, centrally selected judges will struggle in rural sessions.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = true
            ),
            DigestItem(
                category = Category.Environment,
                headline = "Farmers Get Huge Subsidies for Eco-Friendly Stubble Sprays",
                previewText = "Farmers are getting huge subsidies on special eco-friendly enzyme sprays that dissolve leftover crop stalks naturally. This brilliant move aims to clear up winter smog and keep our city skies clean.",
                contextText = "To prevent winter smog, the Commission for Air Quality Management released a strict protocol today. The state will subsidize bio-decomposer capsules to eliminate open field burning.",
                keyPointsText = "1. 80% subsidy on bio-decomposer spray packages.\n2. Mobile spraying units deployed to rural cooperatives.\n3. Satellite tracking of stubble fires with immediate fines.\n4. Farmer-led training camps at village levels.",
                whyItMattersText = "Stubble burning contributes to catastrophic air pollution index levels every autumn, causing severe public health crises in adjacent urban centers.",
                examAngleText = "Syllabus correlation: Air pollution indices (AQI), agricultural residue management, CAQM statutory powers, and organic enzymes.",
                sourceAFraming = "Clean Air Coalition (Environmentalist): Celebrates the mandatory guidelines, hoping that biological solutions will replace the harmful smoke plumes.",
                sourceBFraming = "The Farmer's Voice (Agrarian): Argues that the time window between harvesting and sowing is too short for biological decomposition, demanding mechanical machinery instead.",
                sourceCFraming = "Agro-Scientist (Technical): Notes that enzyme sprays require exact soil moisture to work effectively, which might be lacking during dry seasons.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = true
            ),
            DigestItem(
                category = Category.InternationalRelations,
                headline = "Bilateral Trade Pact Signed to Sidestep the Dollar Reserve",
                previewText = "Two major countries have agreed to trade using their own local currencies instead of the US dollar. This makes buying and selling goods across borders much cheaper and protects small traders from currency swings.",
                contextText = "A historic bilateral clearing mechanism was established today. Importers can now open local accounts directly, lowering transaction costs and hedging against foreign exchange volatility.",
                keyPointsText = "1. Setup of dedicated Nostro and Vostro accounts in partner banks.\n2. Direct currency conversion rates bypassing intermediary dollar indices.\n3. Real-time settlement interface between national clearing systems.\n4. Initial coverage for essential commodities including crude and fertilizers.",
                whyItMattersText = "This represents a major step in global de-dollarization, reducing currency reserves dependency and safeguarding trade from unilateral financial sanctions.",
                examAngleText = "Be prepared to write about International Trade settlement systems, Reserve Currency statuses, Vostro accounts, and global exchange rate mechanisms.",
                sourceAFraming = "Trade Weekly (Global-Commerce): Praises the reduction in transaction fees, making cross-border business highly frictionless for medium-scale merchants.",
                sourceBFraming = "Sovereign Watch (Conservative): Warns that holding non-convertible partner currency increases exchange rate risk if their economy faces internal inflation.",
                sourceCFraming = "Financial Analyst (Analytical): Believes this is a strategic move to secure energy pipelines, though full-scale transition will take decades.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = true
            ),
            DigestItem(
                category = Category.Science,
                headline = "Locally-Made Submarine Matsya-6000 Gearing Up for Ocean Dive",
                previewText = "A highly advanced, locally-made deep ocean submarine named Matsya is gearing up to dive six kilometers under the sea. Three scientists will go along to explore beautiful mineral beds and hidden volcanic vents.",
                contextText = "The National Institute of Ocean Technology successfully concluded harbour trials of Matsya-6000 today. The vessel is designed to withstand extreme hydrostatic pressures in the abyssal zone.",
                keyPointsText = "1. Titanium alloy personnel sphere developed in-house.\n2. Operational endurance of 12 hours under normal exploration routines.\n3. Remote manipulator arms for selective seabed soil sampling.\n4. Real-time acoustic communication links with the mother ship.",
                whyItMattersText = "Exploration of the deep seabed unlocks immense resources of cobalt, nickel, and rare-earth elements essential for clean energy battery storage technologies.",
                examAngleText = "Study Deep Ocean Mission parameters, polymetallic nodules, maritime exclusive economic zones (EEZ), and tectonic hydrothermal vents.",
                sourceAFraming = "Tech Horizon (Nationalist): Celebrates the entry into an elite club of deep-ocean exploration nations, showcasing supreme mechanical engineering capabilities.",
                sourceBFraming = "Blue Ocean Trust (Conservation-First): Expresses deep concern that seabed mining will destroy fragile benthic ecosystems before we can even study them.",
                sourceCFraming = "Oceanic Scholar (Neutral): Affirms that Matsya-6000 represents a major scientific research asset, though commercial mining remains decades away.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = true
            ),
            DigestItem(
                category = Category.Economy,
                headline = "New Quick Insolvency Rules Set to Save Struggling Mid-Sized Businesses",
                previewText = "A new quick-restructuring rule helps medium-sized businesses settle their financial debts in under ninety days. It allows business owners to keep their jobs and assets running during disputes.",
                contextText = "The Ministry of Corporate Affairs notified major amendments to the IBC today. The pre-packaged insolvency scheme allows debtor-in-possession models during reorganization phases.",
                keyPointsText = "1. Maximum resolution window capped strictly at 90 days.\n2. Retained management controls for existing promoters during restructuring.\n3. Minimum approval threshold of 66% from financial creditors.\n4. Safe-harbour protections for operational creditors.",
                whyItMattersText = "By speeding up corporate debt resolution, this keeps capital flowing, prevents viable companies from liquidation, and reduces non-performing assets on banks' balance sheets.",
                examAngleText = "Understand the Insolvency and Bankruptcy Code (IBC), National Company Law Tribunal (NCLT), and corporate debt resolution tools.",
                sourceAFraming = "Business Standard (Corporate-Friendly): Commends the debtor-friendly reforms, preventing unnecessary jobs loss and preserving operational assets during disputes.",
                sourceBFraming = "Creditors Forum (Fiscal-Skeptical): Expresses concern that leaving promoters in control might allow them to shield assets from legitimate creditors.",
                sourceCFraming = "Legal Review (Analytical): Observes that NCLT benches are already backlogged, so procedural timelines must be strictly monitored to make the 90-day cap realistic.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = true
            ),
            DigestItem(
                category = Category.Polity,
                headline = "Draft Digital Personal Data Protection Rules Out for Public Input",
                previewText = "The government has published strict new rules to protect your personal details online from unauthorized tracking. Websites must get your clear permission first or face massive fines for leaks.",
                contextText = "The Ministry of Electronics and Information Technology released the long-awaited draft rules today. The document establishes clear procedures for obtaining explicit, unambiguous consent.",
                keyPointsText = "1. Right to access, update, and completely erase personal data histories.\n2. Heavy financial penalties for unauthorized data breaches or leaks.\n3. Mandated local storage for critical state security data classes.\n4. Creation of an independent national Data Protection Board.",
                whyItMattersText = "With digital services permeating daily life, a robust legal shield is essential to prevent corporate surveillance, protect user privacy, and secure data sovereignty.",
                examAngleText = "Focus on the Right to Privacy under Article 21, the Justice Srikrishna Committee recommendations, and comparison of DPDP with European GDPR.",
                sourceAFraming = "Privacy Now (Civil Liberty): Strongly supports the citizen-first approach, establishing personal data sovereignty as an inalienable human right.",
                sourceBFraming = "Startup Hub (Business-Skeptical): Fears that compliance costs and heavy regulatory fines will choke innovation, especially for cash-strapped early-stage startups.",
                sourceCFraming = "Digital Analyst (Neutral): Notes that the law's effectiveness will depend entirely on the independence and investigative capacity of the new Data Protection Board.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = true
            ),
            DigestItem(
                category = Category.Environment,
                headline = "Ten Major Cities to Grow Fast-Growing Japanese Pocket Forests",
                previewText = "Ten major cities are planting dense miniature forests in empty concrete pockets using a famous Japanese technique. These tiny urban sanctuaries grow ten times faster, cooling down congested local neighborhoods.",
                contextText = "To combat urban heat islands, the Forestry Department launched a city-greening campaign today. Over 100 pocket parks will be planted with dense native saplings.",
                keyPointsText = "1. Multi-tier dense planting using native tree, shrub, and groundcover species.\n2. Up to 10x faster growth and 30x higher density than standard plantations.\n3. Complete self-sufficiency achieved within three years of planting.\n4. Active community participation with local resident upkeep committees.",
                whyItMattersText = "Urban forests absorb carbon, filter fine particulate dust, lower local ambient temperatures, and restore native bird and insect biodiversity in concrete jungles.",
                examAngleText = "Learn about Miyawaki plantation steps, urban heat islands, particulate matter filtering, and climate adaptation strategies.",
                sourceAFraming = "Eco-City (Urbanist): Welcomes the miniature forests, praising the rapid greening of abandoned municipal lots into thriving pocket sanctuaries.",
                sourceBFraming = "The Botanist (Traditional-Conservationist): Cautions that Miyawaki forests are highly artificial and cannot replace the complex ecosystem services of natural, old-growth forests.",
                sourceCFraming = "Urban Planner (Neutral): Supports the initiative but points out that municipal land acquisition in high-density areas remains a major administrative bottleneck.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = true
            ),
            DigestItem(
                category = Category.InternationalRelations,
                headline = "Global Tech Alliance Teams Up to Prevent Smartphone Chip Shortages",
                previewText = "Seven major democracies are teaming up to prevent future smartphone chip shortages by stockpiling raw materials. This ensures a steady pipeline of advanced chips for electronics and defense networks.",
                contextText = "A semiconductor resilience treaty was signed today. The agreement coordinates multi-billion dollar subsidies and guarantees supply security of high-purity chemicals and rare gases.",
                keyPointsText = "1. Harmonized standards for chip fab construction subsidies.\n2. Joint strategic stockpiles of critical gases (neon, krypton) and minerals.\n3. Coordinated talent exchange programs for silicon engineering graduates.\n4. Combined security tracking of global silicon supply lines.",
                whyItMattersText = "Advanced chips power everything from smartphones to satellite guidance systems. Supply chain disruptions can freeze industrial manufacturing and damage defense capabilities globally.",
                examAngleText = "Understand global supply chain resilience, semiconductor manufacturing bottlenecks (lithography, packaging), and strategic trade controls.",
                sourceAFraming = "Silicon Industry (Pro-Tech): Applauds the coordinated push, predicting stable supply pipelines and accelerated development of next-gen fabs.",
                sourceBFraming = "Globalist Review (Free-Market): Warns that massive state subsidies risk distorting market prices and raising trade friction with non-allied nations.",
                sourceCFraming = "Defense Analyst (Security-First): Supports the strategic security focus, arguing that dependency on single-source geographic hotspots is a fatal strategic vulnerability.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = true
            ),
            DigestItem(
                category = Category.Science,
                headline = "Scientists Map Deep Underground Desert Water Using Smart Isotopic Markers",
                previewText = "Scientists are using smart isotopic markers to map out deep ancient groundwater pathways in extremely dry desert regions. This precise mapping helps dry villages manage their scarce drinking water sources safely.",
                contextText = "A hydro-geological survey report was released today. By measuring isotope ratios, researchers identified ancient paleo-channels that are crucial for regional water security.",
                keyPointsText = "1. Mapping of 12 major subterranean aquifers in hyper-arid plains.\n2. Identification of recharge source zones in distant mountain ranges.\n3. Measurement of groundwater ages ranging up to 10,000 years.\n4. Integration of isotopic data with satellite gravity measurements.",
                whyItMattersText = "Understanding aquifer replenishment speeds prevents over-extraction, allowing dry zones to design sustainable crop patterns and secure drinking water supplies.",
                examAngleText = "Study isotope hydrology applications, water cycle dynamics, aquifer recharge mechanics, and sustainable irrigation techniques.",
                sourceAFraming = "Arid Science (Scientific): Hails the mapping as a vital breakthrough, giving dry regions accurate scientific data to manage their scarce water resources.",
                sourceBFraming = "Water Rights Council (Socialist): Warns that mapping aquifers could lead to commercial exploitation by private beverage conglomerates unless strict local regulations are passed.",
                sourceCFraming = "Hydro-Engineer (Technical): Notes that mapping is only the first step; actual groundwater recharge policies require massive civil infrastructure changes.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = true
            ),
            DigestItem(
                category = Category.Other,
                headline = "National Heritage Mission to Restore 100 Beautiful Ancient Stepwells",
                previewText = "A major national campaign has started to revive over a hundred historical stepwells across five states. Clearing silt from these architectural marvels helps raise local water levels and boosts heritage tourism.",
                contextText = "The Ministry of Culture launched the Stepwell Revival Project today. The initiative will clear silt, repair historic masonry, and reconnect ancient catchments.",
                keyPointsText = "1. Detailed structural restoration of 100 iconic stepwells across five states.\n2. De-silting and restoration of underground spring inflows.\n3. Conversion of stepwell surrounds into community cultural centers.\n4. Training of local youths as heritage guides and conservationists.",
                whyItMattersText = "Stepwells are architectural marvels that served as traditional rainwater reservoirs. Reviving them restores local water tables while boosting heritage tourism and community pride.",
                examAngleText = "Focus on ancient water harvesting technologies in India (Stepwells/Baolis), architectural styles of dry zones, and UNESCO heritage criteria.",
                sourceAFraming = "Heritage Gazette (Cultural): Delighted by the project, celebrating the preservation of these subterranean temples and ancient engineering wisdom.",
                sourceBFraming = "Civic Voice (Pragmatic): Strongly supports the restoration but emphasizes that surrounding urban sewer networks must be fixed first to prevent dirty water inflows.",
                sourceCFraming = "Archaeological Journal (Neutral): Agrees that the project is highly valuable, but advises that scientific conservation standards must be maintained over simple beautification.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = true
            ),
            DigestItem(
                category = Category.Economy,
                headline = "Huge Three-Billion-Dollar Incentive Approved for Building Local Battery Factories",
                previewText = "A massive three-billion-dollar incentive scheme was approved to build huge battery factories locally. This brilliant push will make electric cars and home solar backup systems way cheaper.",
                contextText = "The Union Cabinet approved the revised PLI battery manufacturing scheme today. The program offers graded cash incentives for companies achieving high domestic value addition.",
                keyPointsText = "1. $3 Billion budget to support 50 Giga-Watt hours of cell production.\n2. Mandatory target of 60% domestic value addition within five years.\n3. Incentives linked directly to cell energy density and safety performance metrics.\n4. Fast-track single-window clearances for greenfield gigafabs.",
                whyItMattersText = "Localizing advanced chemistry cell manufacturing dramatically lowers the cost of electric vehicles and grid storage units, driving green energy transitions.",
                examAngleText = "Syllabus focus: PLI scheme mechanics, battery technologies (Lithium-ion, Solid-state, Sodium-ion), and domestic manufacturing competitiveness.",
                sourceAFraming = "EV Pioneer (Industrial): Thrilled by the announcement, predicting that local cell production will make electric vehicles highly affordable and competitive.",
                sourceBFraming = "Taxpayer Sentinel (Fiscal-Conservative): Warns that long-term state subsidies are a heavy burden on taxpayers, urging caution over pick-the-winner industrial strategies.",
                sourceCFraming = "Battery Researcher (Technical): Notes that while subsidies are useful, local mining and refining of battery minerals (lithium, cobalt) is the actual bottleneck that must be resolved.",
                date = today,
                isBookmarked = false,
                isRead = false,
                isDailyPulse = true
            )
        )
    }

    fun getSeedEditorialItems(): List<EditorialItem> {
        val today = getTodayDateString()
        return listOf(
            EditorialItem(
                title = "The Slow Journalism Revolution: Reclaiming Focus in a Hyper-Connected World",
                takeawayText = "Ditching infinite scrolling is not just an aesthetic choice — it is a cognitive necessity for high-level analytical thinking and citizen engagement.",
                fullAnalysisText = "In the contemporary media landscape, the competition for human attention has resulted in the 'attention-economy' model, where news apps are structured to maximize dwell time and click-rates. Algorithms prioritize highly emotional headlines and infinite scrolling structures, encouraging rapid scanning rather than critical comprehension.\n\nThis continuous bombardment of fragmented information triggers cognitive overload. Under these conditions, the human brain struggles to move data from short-term working memory to deep, long-term conceptual schemas. The result is a population that knows 'what' is happening but rarely understands 'why' or 'how'.\n\nSlow journalism represents a deliberate counter-cultural movement. By providing bounded, curated, and calm news summaries daily, we respect the reader's cognitive limits. Spending 5 dedicated, high-focus minutes reading deep, structured contexts is infinitely more valuable for intellectual growth than an hour of mindless doom-scrolling. It allows citizens to develop coherent viewpoints and engage in rational, analytical civil discourse, rebuilding the foundations of a healthy society.",
                date = today
            ),
            EditorialItem(
                title = "Fiscal Federalism vs. Regional Autonomy: The Tug-of-War in Modern State Budgets",
                takeawayText = "Healthy democracies require a delicate balance between standardized central macro-planning and flexible regional self-funding powers.",
                fullAnalysisText = "The debate over central government allocations vs. regional self-raising capacity is as old as modern democracy itself. While central planning ensures baseline equity and infrastructure standard safety across all regions, local bodies are the ones directly dealing with localized civic challenges — from sewage systems to local primary school resources.\n\nOver the past decade, tax centralization has steadily stripped state and municipal bodies of direct revenue channels, making them structurally dependent on central grants. This centralization of fiscal authority weakens local accountability; if local leaders have no control over their budgets, citizens lose their direct leverage to demand neighborhood public improvements.\n\nRebalancing fiscal federalism requires innovative mechanisms. Municipal bonds, decentralized public trusts, and biometric audit frameworks represent tools that can bridge the gap. By allowing cities to borrow capital directly based on transparent credit scoring, we can restore administrative autonomy and incentivize fiscal responsibility. However, the center must remain as a strong, neutral regulatory watchdog, ensuring that regional experiments do not lead to systemic, national financial vulnerabilities.",
                date = today
            ),
            EditorialItem(
                title = "The Digital Public Infrastructure (DPI) Model: Redefining Citizen-State Trust and Economic Mobility",
                takeawayText = "Digital Public Infrastructure (DPI) must be approached not merely as a tech-stack solution, but as a socio-economic catalyst. For a DPI to be truly successful, it requires three structural pillars: open-source interoperable rails, a robust independent data-protection authority to prevent state/corporate surveillance, and universal offline access to bridge the digital divide. Rather than top-down proprietary platforms, modern digital governance must foster public-private-social partnerships that democratize market access, secure individual consent, and establish digital identities as an inalienable fundamental right rather than a commercial commodity.",
                fullAnalysisText = "Over the last decade, the concept of Digital Public Infrastructure (DPI) has emerged as a cornerstone of modern state-building, particularly in emerging economies. By designing open APIs for payments, identity verification, and data sharing, countries have leapfrogged traditional banking and administrative structures, bringing millions of unbanked citizens into the formal financial ecosystem.\n\nHyper-connected state-led platforms have accelerated financial inclusion, but they also raise critical privacy concerns. When the state holds the master keys to digital identity, transaction flows, and biometric databases, the capacity for centralized overreach increases exponentially. Absolute protection of personal data and distributed ownership of cryptographic keys are the only ways to prevent digital public infrastructure from transforming into a tool of political and social coercion.",
                date = today
            )
        )
    }

    fun getSeedQuizQuestions(): List<QuizQuestion> {
        val today = getTodayDateString()
        return listOf(
            QuizQuestion(
                date = today,
                questionText = "Under the proposed Municipal Decentralization Bill, financial autonomy is granted to municipalities exceeding what population threshold?",
                option1 = "500,000",
                option2 = "1 Million",
                option3 = "5 Million",
                option4 = "10 Million",
                correctOptionIndex = 1
            ),
            QuizQuestion(
                date = today,
                questionText = "What is the primary monetary policy tool used by the central bank to control liquidity, which was paused at 6.25% today?",
                option1 = "Cash Reserve Ratio (CRR)",
                option2 = "Statutory Liquidity Ratio (SLR)",
                option3 = "Repo Rate",
                option4 = "Marginal Standing Facility",
                correctOptionIndex = 2
            ),
            QuizQuestion(
                date = today,
                questionText = "Which convention is primarily concerned with the international preservation and sustainable use of wetland habitats?",
                option1 = "Ramsar Convention",
                option2 = "Kyoto Protocol",
                option3 = "Basel Convention",
                option4 = "Stockholm Convention",
                correctOptionIndex = 0
            ),
            QuizQuestion(
                date = today,
                questionText = "At what supercooled temperature did physicists achieve a stable 256-qubit quantum coherence state in the new study?",
                option1 = "0 Kelvin",
                option2 = "0.5 Kelvin",
                option3 = "77 Kelvin",
                option4 = "273 Kelvin",
                correctOptionIndex = 1
            ),
            QuizQuestion(
                date = today,
                questionText = "The Global Soil Health Accord aims to restore topsoil organic carbon content to at least 3% by which target year?",
                option1 = "2030",
                option2 = "2035",
                option3 = "2040",
                option4 = "2050",
                correctOptionIndex = 2
            )
        )
    }
}

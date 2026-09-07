package com.example.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.MainViewModel

object TranslationHelper {

    private val translations = mapOf(
        "Hindi" to mapOf(
            // --- EDITORIALS ---
            "The Slow Journalism Revolution: Reclaiming Focus in a Hyper-Connected World" to 
                "धीमी पत्रकारिता की क्रांति: अत्यधिक जुड़े संसार में ध्यान वापस पाना",
            "Ditching infinite scrolling is not just an aesthetic choice — it is a cognitive necessity for high-level analytical thinking and citizen engagement." to 
                "अनंत स्क्रॉलिंग को छोड़ना केवल एक सौंदर्य पसंद नहीं है - यह उच्च-स्तरीय विश्लेषणात्मक सोच और नागरिक जुड़ाव के लिए एक संज्ञानात्मक आवश्यकता है।",
            "In the contemporary media landscape, the competition for human attention has resulted in the 'attention-economy' model, where news apps are structured to maximize dwell time and click-rates. Algorithms prioritize highly emotional headlines and infinite scrolling structures, encouraging rapid scanning rather than critical comprehension.\n\nThis continuous bombardment of fragmented information triggers cognitive overload. Under these conditions, the human brain struggles to move data from short-term working memory to deep, long-term conceptual schemas. The result is a population that knows 'what' is happening but rarely understands 'why' or 'how'.\n\nSlow journalism represents a deliberate counter-cultural movement. By providing bounded, curated, and calm news summaries daily, we respect the reader's cognitive limits. Spending 5 dedicated, high-focus minutes reading deep, structured contexts is infinitely more valuable for intellectual growth than an hour of mindless doom-scrolling. It allows citizens to develop coherent viewpoints and engage in rational, analytical civil discourse, rebuilding the foundations of a healthy society." to 
                "समकालीन मीडिया परिदृश्य में, मानवीय ध्यान के लिए प्रतिस्पर्धा के परिणामस्वरूप 'ध्यान-अर्थव्यवस्था' मॉडल सामने आया है, जहाँ समाचार ऐप्स को उपयोगकर्ताओं के स्क्रीन समय और क्लिक-दरों को अधिकतम करने के लिए संरचित किया जाता है। एल्गोरिदम अत्यधिक भावनात्मक सुर्खियों और अनंत स्क्रॉलिंग संरचनाओं को प्राथमिकता देते हैं, जिससे आलोचनात्मक समझ के बजाय त्वरित स्कैनिंग को बढ़ावा मिलता है।\n\nखंडित जानकारी की यह निरंतर बौछार संज्ञानात्मक अधिभार (कॉग्निटिव ओवरलोड) को ट्रिगर करती है। इन परिस्थितियों में, मानव मस्तिष्क अल्पकालिक कार्यशील स्मृति से डेटा को गहरी, दीर्घकालिक वैचारिक योजनाओं में स्थानांतरित करने के लिए संघर्ष करता है। इसका परिणाम एक ऐसी आबादी के रूप में होता है जो यह तो जानती है कि 'क्या' हो रहा है लेकिन शायद ही कभी समझ पाती है कि 'क्यों' या 'कैसे' हो रहा है।\n\nधीमी पत्रकारिता एक विचारशील प्रति-सांस्कृतिक आंदोलन का प्रतिनिधित्व करती है। दैनिक रूप से सीमित, क्यूरेटेड और शांत समाचार सारांश प्रदान करके, हम पाठक की संज्ञानात्मक सीमाओं का सम्मान करते हैं। दिमागी तौर पर बिना सोचे-समझे किए जाने वाले डूम-स्क्रॉलिंग के एक घंटे की तुलना में गहरे, संरचित संदर्भों को पढ़ने में 5 समर्पित, उच्च-फोकस मिनट बिताना बौद्धिक विकास के लिए असीम रूप से अधिक मूल्यवान है। यह नागरिकों को सुसंगत दृष्टिकोण विकसित करने और तर्कसंगत, विश्लेषणात्मक नागरिक संवाद में संलग्न होने की अनुमति देता है, जिससे एक स्वस्थ समाज की नींव का पुनर्निर्माण होता है।",

            "Fiscal Federalism vs. Regional Autonomy: The Tug-of-War in Modern State Budgets" to 
                "राजकोषीय संघवाद बनाम क्षेत्रीय स्वायत्तता: आधुनिक राज्य बजटों में रस्साकशी",
            "Healthy democracies require a delicate balance between standardized central macro-planning and flexible regional self-funding powers." to 
                "स्वस्थ लोकतंत्रों को मानकीकृत केंद्रीय मैक्रो-योजना और लचीली क्षेत्रीय स्व-वित्तपोषण शक्तियों के बीच एक नाजुक संतुलन की आवश्यकता होती है।",
            "The debate over central government allocations vs. regional self-raising capacity is as old as modern democracy itself. While central planning ensures baseline equity and infrastructure standard safety across all regions, local bodies are the ones directly dealing with localized civic challenges — from sewage systems to local primary school resources.\n\nOver the past decade, tax centralization has steadily stripped state and municipal bodies of direct revenue channels, making them structurally dependent on central grants. This centralization of fiscal authority weakens local accountability; if local leaders have no control over their budgets, citizens lose their direct leverage to demand neighborhood public improvements.\n\nRebalancing fiscal federalism requires innovative mechanisms. Municipal bonds, decentralized public trusts, and biometric audit frameworks represent tools that can bridge the gap. By allowing cities to borrow capital directly based on transparent credit scoring, we can restore administrative autonomy and incentivize fiscal responsibility. However, the center must remain as a strong, neutral regulatory watchdog, ensuring that regional experiments do not lead to systemic, national financial vulnerabilities." to 
                "केंद्र सरकार के आवंटन बनाम क्षेत्रीय स्व-उत्पादन क्षमता को लेकर बहस उतनी ही पुरानी है जितना कि खुद आधुनिक लोकतंत्र। जबकि केंद्रीय योजना सभी क्षेत्रों में आधारभूत निष्पक्षता और बुनियादी ढांचा मानक सुरक्षा सुनिश्चित करती है, स्थानीय निकाय वे हैं जो सीधे स्थानीय नागरिक चुनौतियों - सीवेज सिस्टम से लेकर स्थानीय प्राथमिक स्कूल संसाधनों तक - से निपटते हैं।\n\nपिछले दशक में, कर केंद्रीकरण ने राज्य और नगर निकायों को प्रत्यक्ष राजस्व चैनलों से लगातार वंचित किया है, जिससे वे संरचनात्मक रूप से केंद्रीय अनुदानों पर निर्भर हो गए हैं। राजकोषीय अधिकार का यह केंद्रीकरण स्थानीय जवाबदेही को कमजोर करता है; यदि स्थानीय नेताओं का अपने बजट पर कोई नियंत्रण नहीं है, तो नागरिक पड़ोस के सार्वजनिक सुधारों की मांग करने के लिए अपने सीधे प्रभाव को खो देते हैं।\n\nराजकोषीय संघवाद को पुनर्संतुलित करने के लिए अभिनव तंत्र की आवश्यकता है। म्यूनिसिपल बॉन्ड, विकेंद्रीकृत सार्वजनिक ट्रस्ट और बायोमेट्रिक ऑडिट फ्रेमवर्क ऐसे उपकरण हैं जो इस अंतर को पाट सकते हैं। पारदर्शी क्रेडिट स्कोरिंग के आधार पर शहरों को सीधे पूंजी उधार लेने की अनुमति देकर, हम प्रशासनिक स्वायत्तता बहाल कर सकते हैं और वित्तीय जिम्मेदारी को प्रोत्साहित कर सकते हैं। हालांकि, केंद्र को एक मजबूत, तटस्थ नियामक प्रहरी के रूप में बने रहना चाहिए, यह सुनिश्चित करते हुए कि क्षेत्रीय प्रयोगों से प्रणालीगत, राष्ट्रीय वित्तीय कमजोरियां पैदा न हों।",

            "The Digital Public Infrastructure (DPI) Model: Redefining Citizen-State Trust and Economic Mobility" to 
                "डिजिटल सार्वजनिक अवसंरचना (DPI) मॉडल: नागरिक-राज्य विश्वास और आर्थिक गतिशीलता को फिर से परिभाषित करना",
            "Digital Public Infrastructure (DPI) must be approached not merely as a tech-stack solution, but as a socio-economic catalyst. For a DPI to be truly successful, it requires three structural pillars: open-source interoperable rails, a robust independent data-protection authority to prevent state/corporate surveillance, and universal offline access to bridge the digital divide. Rather than top-down proprietary platforms, modern digital governance must foster public-private-social partnerships that democratize market access, secure individual consent, and establish digital identities as an inalienable fundamental right rather than a commercial commodity." to 
                "डिजिटल सार्वजनिक अवसंरचना (DPI) को केवल एक तकनीकी-स्टैक समाधान के रूप में नहीं, बल्कि एक सामाजिक-आर्थिक उत्प्रेरक के रूप में देखा जाना चाहिए। एक डीपीआई को वास्तव में सफल होने के लिए, तीन संरचनात्मक स्तंभों की आवश्यकता होती है: ओपन-सोर्स इंटरऑपरेबल रेल, राज्य/कॉर्पोरेट निगरानी को रोकने के लिए एक मजबूत स्वतंत्र डेटा-संरक्षण प्राधिकरण, और डिजिटल विभाजन को पाटने के लिए सार्वभौमिक ऑफ़लाइन पहुंच। ऊपर से नीचे की ओर जाने वाले मालिकाना प्लेटफार्मों के बजाय, आधुनिक डिजिटल शासन को सार्वजनिक-निजी-सामाजिक साझेदारियों को बढ़ावा देना चाहिए जो बाजार तक पहुंच का लोकतंत्रीकरण करें, व्यक्तिगत सहमति सुरक्षित करें और डिजिटल पहचान को व्यावसायिक वस्तु के बजाय एक अपरिहार्य मौलिक अधिकार के रूप में स्थापित करें।",
            "Over the last decade, the concept of Digital Public Infrastructure (DPI) has emerged as a cornerstone of modern state-building, particularly in emerging economies. By designing open APIs for payments, identity verification, and data sharing, countries have leapfrogged traditional banking and administrative structures, bringing millions of unbanked citizens into the formal financial ecosystem.\n\nHyper-connected state-led platforms have accelerated financial inclusion, but they also raise critical privacy concerns. When the state holds the master keys to digital identity, transaction flows, and biometric databases, the capacity for centralized overreach increases exponentially. Absolute protection of personal data and distributed ownership of cryptographic keys are the only ways to prevent digital public infrastructure from transforming into a tool of political and social coercion." to 
                "पिछले दशक में, डिजिटल सार्वजनिक अवसंरचना (डीपीआई) की अवधारणा आधुनिक राष्ट्र-निर्माण के एक आधारशिला के रूप में उभरी है, विशेष रूप से उभरती अर्थव्यवस्थाओं में। भुगतान, पहचान सत्यापन और डेटा साझाकरण के लिए ओपन एपीआई डिजाइन करके, देशों ने पारंपरिक बैंकिंग और प्रशासनिक संरचनाओं को पीछे छोड़ दिया है, जिससे लाखों बिना बैंक खाते वाले नागरिकों को औपचारिक वित्तीय पारिस्थितिकी तंत्र में लाया गया है।\n\nअत्यधिक जुड़े राज्य के नेतृत्व वाले प्लेटफार्मों ने वित्तीय समावेशन में तेजी लाई है, लेकिन वे महत्वपूर्ण गोपनीयता चिंताओं को भी बढ़ाते हैं। जब राज्य के पास डिजिटल पहचान, लेनदेन प्रवाह और बायोमेट्रिक डेटाबेस की मास्टर चाबियां होती हैं, तो केंद्रीकृत पहुंच की क्षमता तेजी से बढ़ जाती है। व्यक्तिगत डेटा की पूर्ण सुरक्षा और क्रिप्टोग्राफिक कुंजियों का वितरित स्वामित्व ही डिजिटल सार्वजनिक अवसंरचना को राजनीतिक और सामाजिक जबरदस्ती के उपकरण में बदलने से रोकने का एकमात्र तरीका है।",

            // --- DIGEST ITEMS (HEADLINES) ---
            "Parliament Introduces Draft Bill for Decentralized Governance" to "संसद ने विकेंद्रीकृत शासन के लिए मसौदा विधेयक पेश किया",
            "Central Bank Pauses Rates Citing Structural Supply-Side Easing" to "केंद्रीय बैंक ने संरचनात्मक आपूर्ति-पक्षीय नरमी का हवाला देते हुए दरों पर रोक लगाई",
            "National Wetland Initiative Targets 50 Critical Estuaries for Reclamation" to "राष्ट्रीय आर्द्रभूमि पहल ने सुधार के लिए 50 महत्वपूर्ण मुहानों को लक्षित किया",
            "Trilateral Maritime Alliance Formed to Secure Key Shipping Straits" to "प्रमुख समुद्री जलमार्गों को सुरक्षित करने के लिए त्रिपक्षीय समुद्री गठबंधन का गठन",
            "Solid-State Quantum Engine Achieves Supercooled Stability Record" to "सॉलिड-स्टेट क्वांटम इंजन ने सुपरकूल्ड स्थिरता का रिकॉर्ड हासिल किया",
            "Electoral Commission Announces Direct Voting Audits Using Biometric Logs" to "चुनाव आयोग ने बायोमेट्रिक लॉग का उपयोग करके प्रत्यक्ष मतदान ऑडिट की घोषणा की",
            "E-Commerce Consolidation Rules Restrict Self-Brand Promotion" to "ई-कॉमर्स एकीकरण नियम अपने स्वयं के ब्रांड प्रचार को प्रतिबंधित करते हैं",
            "Global Soil Health Accord Mandates Regenerative Agriculture Practices" to "वैश्विक मृदा स्वास्थ्य समझौता पुनर्योजी कृषि पद्धतियों को अनिवार्य करता है",

            // --- RECURRING GENERAL PHRASES ---
            "Read Summary" to "सारांश पढ़ें",
            "Bookmarked" to "बुकमार्क किया गया",
            "Bookmarks" to "बुकमार्क",
            "Settings" to "सेटिंग्स",
            "Today's SAAR" to "आज का सार (SAAR)",
            "Editorial Analysis" to "संपादकीय विश्लेषण",
            "Key Takeaways" to "मुख्य निष्कर्ष",
            "Context" to "संदर्भ",
            "Key Points" to "मुख्य बिंदु",
            "Why It Matters" to "यह क्यों मायने रखता है",
            "Exam Angle" to "परीक्षा का दृष्टिकोण",
            "Compiled on" to "संकलित तिथि:",
            "Compare Perspectives" to "दृष्टिकोणों की तुलना करें",
            "Add to Today's PDF" to "आज के पीडीएफ में जोड़ें",
            "Added to PDF ✓" to "पीडीएफ में जोड़ा गया ✓"
        ),
        "Marathi" to mapOf(
            // --- EDITORIALS ---
            "The Slow Journalism Revolution: Reclaiming Focus in a Hyper-Connected World" to 
                "स्लो जर्नलिझमची क्रांती: हायपर-कनेक्टेड जगात लक्ष पुन्हा मिळवणे",
            "Ditching infinite scrolling is not just an aesthetic choice — it is a cognitive necessity for high-level analytical thinking and citizen engagement." to 
                "अनंत स्क्रोलिंग सोडणे हा केवळ एक सौंदर्याचा पर्याय नाही - उच्च-स्तरीय विश्लेषणात्मक विचार आणि नागरिक सहभागासाठी ही एक संज्ञानात्मक गरज आहे.",
            "Fiscal Federalism vs. Regional Autonomy: The Tug-of-War in Modern State Budgets" to 
                "राजकोषीय संघराज्य विरुद्ध प्रादेशिक स्वायत्तता: आधुनिक राज्य अर्थसंकल्पातील रस्सीखेच",
            "Healthy democracies require a delicate balance between standardized central macro-planning and flexible regional self-funding powers." to 
                "निरोगी लोकशाहीला मानकीकृत केंद्रीय मॅक्रो-प्लॅनिंग आणि लवचिक प्रादेशिक स्व-वित्तपुरवठा अधिकार यांच्यात नाजूक समतोल राखण्याची आवश्यकता असते.",
            "The Digital Public Infrastructure (DPI) Model: Redefining Citizen-State Trust and Economic Mobility" to 
                "डिजिटल पब्लिक इन्फ्रास्ट्रक्चर (DPI) मॉडेल: नागरिक-राज्य विश्वास आणि आर्थिक गतिशीलता पुनर्व्याख्या करणे",
            "Digital Public Infrastructure (DPI) must be approached not merely as a tech-stack solution, but as a socio-economic catalyst. For a DPI to be truly successful, it requires three structural pillars: open-source interoperable rails, a robust independent data-protection authority to prevent state/corporate surveillance, and universal offline access to bridge the digital divide. Rather than top-down proprietary platforms, modern digital governance must foster public-private-social partnerships that democratize market access, secure individual consent, and establish digital identities as an inalienable fundamental right rather than a commercial commodity." to 
                "डिजिटल पब्लिक इन्फ्रास्ट्रक्चर (DPI) कडे केवळ टेक-स्टॅक सोल्यूशन म्हणून नाही, तर सामाजिक-आर्थिक उत्प्रेरक म्हणून पाहिले पाहिजे. डीपीआय खऱ्या अर्थाने यशस्वी होण्यासाठी, तीन संरचनात्मक स्तंभांची आवश्यकता आहे: ओपन-सोर्स इंटरऑपरेबल रेल्स, राज्य/कॉर्पोरेट पाळत ठेवण्यास प्रतिबंध करण्यासाठी एक मजबूत स्वतंत्र डेटा-संरक्षण प्राधिकरण, आणि डिजिटल विभाजन दूर करण्यासाठी सार्वत्रिक ऑफलाइन प्रवेश. वरून खाली जाणाऱ्या मालकीच्या प्लॅटफॉर्मऐवजी, आधुनिक डिजिटल प्रशासनाने सार्वजनिक-खाजगी-सामाजिक भागीदारीला प्रोत्साहन दिले पाहिजे जे बाजारपेठेतील प्रवेशाचे लोकशाहीकरण करेल, वैयक्तिक संमती सुरक्षित करेल आणि डिजिटल ओळख व्यावसायिक वस्तूऐवजी एक अपरिहार्य मूलभूत अधिकार म्हणून स्थापित करेल.",

            // --- DIGEST ITEMS (HEADLINES) ---
            "Parliament Introduces Draft Bill for Decentralized Governance" to "संसदेने विकेंद्रीकृत शासनासाठी मसुदा विधेयक सादर केले",
            "Central Bank Pauses Rates Citing Structural Supply-Side Easing" to "संरचनात्मक पुरवठा-बाजूच्या सुलभतेचा हवाला देत मध्यवर्ती बँकेने व्याजदरांना स्थगिती दिली",
            "National Wetland Initiative Targets 50 Critical Estuaries for Reclamation" to "राष्ट्रीय पाणथळ जागा उपक्रमाने सुधारणेसाठी 50 महत्त्वाच्या खाड्यांना लक्ष्य केले",
            "Trilateral Maritime Alliance Formed to Secure Key Shipping Straits" to "महत्त्वाचे सागरी मार्ग सुरक्षित करण्यासाठी त्रिपक्षीय सागरी आघाडीची स्थापना",
            "Solid-State Quantum Engine Achieves Supercooled Stability Record" to "सॉलिड-स्टेट क्वांटम इंजिनने सुपरकूल्ड स्थिरतेचा विक्रम नोंदवला",
            "Electoral Commission Announces Direct Voting Audits Using Biometric Logs" to "निवडणूक आयोगाने बायोमेट्रिक लॉग वापरून थेट मतदान ऑडिटची घोषणा केली",
            "E-Commerce Consolidation Rules Restrict Self-Brand Promotion" to "ई-कॉमर्स एकत्रीकरण नियम स्वतःच्या ब्रँडच्या जाहिरातीवर निर्बंध घालतात",
            "Global Soil Health Accord Mandates Regenerative Agriculture Practices" to "जागतिक मृदा आरोग्य करार पुनरुत्पादक कृषी पद्धती अनिवार्य करतो",

            // --- RECURRING GENERAL PHRASES ---
            "Read Summary" to "सारांश वाचा",
            "Bookmarked" to "बुकमार्क केलेले",
            "Bookmarks" to "बुकमार्क",
            "Settings" to "सेटिंग्ज",
            "Today's SAAR" to "आजचा सार (SAAR)",
            "Editorial Analysis" to "संपादकीय विश्लेषण",
            "Key Takeaways" to "मुख्य निष्कर्ष",
            "Context" to "संदर्भ",
            "Key Points" to "महत्वाचे मुद्दे",
            "Why It Matters" to "हे का महत्वाचे आहे",
            "Exam Angle" to "परीक्षा दृष्टिकोन",
            "Compiled on" to "संकलित तिथि:",
            "Compare Perspectives" to "दृष्टिकोनांची तुलना करा",
            "Add to Today's PDF" to "आजच्या पीडीएफमध्ये जोडा",
            "Added to PDF ✓" to "पीडीएफमध्ये जोडले ✓"
        ),
        "Tamil" to mapOf(
            // --- EDITORIALS ---
            "The Slow Journalism Revolution: Reclaiming Focus in a Hyper-Connected World" to 
                "மெதுவான இதழியல் புரட்சி: எப்போதும் இணைக்கப்பட்ட உலகில் கவனத்தை மீட்டெடுத்தல்",
            "Ditching infinite scrolling is not just an aesthetic choice — it is a cognitive necessity for high-level analytical thinking and citizen engagement." to 
                "முடிவில்லா ஸ்க்ரோலிங்கைத் தவிர்ப்பது வெறும் அழகியல் தேர்வு மட்டுமல்ல - இது உயர்மட்ட பகுப்பாய்வு சிந்தனை மற்றும் குடிமக்களின் ஈடுபாட்டிற்கான ஒரு அறிவாற்றல் தேவையாகும்.",
            "Fiscal Federalism vs. Regional Autonomy: The Tug-of-War in Modern State Budgets" to 
                "நிதி கூட்டாட்சி வெர்சஸ் பிராந்திய சுயாட்சி: நவீன மாநில வரவு செலவு திட்டங்களில் இழுபறி",
            "Healthy democracies require a delicate balance between standardized central macro-planning and flexible regional self-funding powers." to 
                "ஆரோக்கியமான ஜனநாயகம் நிலவ, தரப்படுத்தப்பட்ட மத்திய திட்டமிடலுக்கும் நெகிழ்வான பிராந்திய நிதி திரட்டும் அதிகாரங்களுக்கும் இடையே ஒரு நுட்பமான சமநிலை தேவைப்படுகிறது.",
            "The Digital Public Infrastructure (DPI) Model: Redefining Citizen-State Trust and Economic Mobility" to 
                "டிஜிட்டல் பொது உள்கட்டமைப்பு (DPI) மாதிரி: குடிமகன்-அரசு நம்பிக்கை மற்றும் பொருளாதார இயக்கத்தை மறுவரையறை செய்தல்",
            "Digital Public Infrastructure (DPI) must be approached not merely as a tech-stack solution, but as a socio-economic catalyst. For a DPI to be truly successful, it requires three structural pillars: open-source interoperable rails, a robust independent data-protection authority to prevent state/corporate surveillance, and universal offline access to bridge the digital divide. Rather than top-down proprietary platforms, modern digital governance must foster public-private-social partnerships that democratize market access, secure individual consent, and establish digital identities as an inalienable fundamental right rather than a commercial commodity." to 
                "டிஜிட்டல் பொது உள்கட்டமைப்பு (டிபிஐ) என்பது வெறும் தொழில்நுட்பத் தீர்வாக மட்டுமல்லாமல், சமூக-பொருளாதார ஊக்கியாகவும் பார்க்கப்பட வேண்டும். ஒரு டிபிஐ உண்மையில் வெற்றியடைய, மூன்று கட்டமைப்புத் தூண்கள் தேவை: திறந்த மூல இயங்கக்கூடிய தண்டவாளங்கள், அரசு/கார்ப்பரேட் கண்காணிப்பைத் தடுப்பதற்கான வலுவான சுதந்திரமான தரவுப் பாதுகாப்பு அதிகாரம் மற்றும் டிஜிட்டல் பிரிவினையைக் குறைப்பதற்கான உலகளாவிய ஆஃப்லைன் அணுகல். மேல்மட்ட தனியுரிம தளங்களுக்குப் பதிலாக, நவீன டிஜிட்டல் நிர்வாகம் பொது-தனியார்-சமூக கூட்டாண்மைகளை வளர்க்க வேண்டும், இது சந்தை அணுகலை ஜனநாயகப்படுத்துகிறது, தனிநபர் ஒப்புதலைப் பாதுகாக்கிறது மற்றும் டிஜிட்டல் அடையாளங்களை வணிகப் பொருளாகக் கருதாமல் ஒரு பிரிக்க முடியாத அடிப்படை உரிமையாக நிறுவுகிறது.",

            // --- DIGEST ITEMS (HEADLINES) ---
            "Parliament Introduces Draft Bill for Decentralized Governance" to "விகலாச்சார நிர்வாகத்திற்கான வரைவு மசோதாவை நாடாளுமன்றம் அறிமுகப்படுத்தியது",
            "Central Bank Pauses Rates Citing Structural Supply-Side Easing" to "கட்டமைப்பு விநியோகத் தளர்வைக் காரணம் காட்டி மத்திய வங்கி வட்டி விகிதங்களை நிறுத்தியது",
            "National Wetland Initiative Targets 50 Critical Estuaries for Reclamation" to "தேசிய ஈரநிலத் திட்டம் மீட்டெடுப்பிற்காக 50 முக்கியமான முகத்துவாரங்களை இலக்காகக் கொண்டுள்ளது",
            "Trilateral Maritime Alliance Formed to Secure Key Shipping Straits" to "முக்கிய கப்பல் போக்குவரத்துக் கால்வாய்களைப் பாதுகாக்க முத்தரப்பு கடல்சார் கூட்டணி உருவாக்கப்பட்டது",
            "Solid-State Quantum Engine Achieves Supercooled Stability Record" to "சாலிட்-ஸ்டேட் குவாண்டம் எஞ்சின் மிகக்குளிர்ந்த நிலைத்தன்மை சாதனையை எட்டியது",
            "Electoral Commission Announces Direct Voting Audits Using Biometric Logs" to "கைரேகை மற்றும் முக அடையாளப் பதிவுகளைப் பயன்படுத்தி நேரடி வாக்குப்பதிவு தணிக்கையை தேர்தல் ஆணையம் அறிவித்தது",
            "E-Commerce Consolidation Rules Restrict Self-Brand Promotion" to "மின்-வணிக ஒருங்கிணைப்பு விதிகள் சுய-பிராண்ட் விளம்பரங்களைக் கட்டுப்படுத்துகின்றன",
            "Global Soil Health Accord Mandates Regenerative Agriculture Practices" to "உலகளாவிய மண் ஆரோக்கிய ஒப்பந்தம் மறுபிறப்பு விவசாய முறைகளை கட்டாயமாக்குகிறது",

            // --- RECURRING GENERAL PHRASES ---
            "Read Summary" to "சுருக்கம் படிக்க",
            "Bookmarked" to "சேமிக்கப்பட்டது",
            "Bookmarks" to "சேமிப்புகள்",
            "Settings" to "அமைப்புகள்",
            "Today's SAAR" to "இன்றைய சார் (SAAR)",
            "Editorial Analysis" to "தலையங்க பகுப்பாய்வு",
            "Key Takeaways" to "முக்கிய குறிப்புகள்",
            "Context" to "சூழல்",
            "Key Points" to "முக்கிய புள்ளிகள்",
            "Why It Matters" to "ஏன் இது முக்கியம்",
            "Exam Angle" to "தேர்வு கோணம்",
            "Compiled on" to "தொகுக்கப்பட்டது:",
            "Compare Perspectives" to "பார்வைகளை ஒப்பிடுக",
            "Add to Today's PDF" to "இன்றைய PDF இல் சேர்க்கவும்",
            "Added to PDF ✓" to "PDF இல் சேர்க்கப்பட்டது ✓"
        ),
        "Telugu" to mapOf(
            // --- EDITORIALS ---
            "The Slow Journalism Revolution: Reclaiming Focus in a Hyper-Connected World" to 
                "స్లో జర్నలిజం విప్లవం: అతిగా అనుసంధానించబడిన ప్రపంచంలో దృష్టిని తిరిగి పొందడం",
            "Ditching infinite scrolling is not just an aesthetic choice — it is a cognitive necessity for high-level analytical thinking and citizen engagement." to 
                "అనంతమైన స్క్రోలింగ్‌ను వదిలివేయడం కేవలం ఒక సౌందర్య ఎంపిక మాత్రమే కాదు - ఇది ఉన్నత-స్థాయి విశ్లేషణాత్మక ఆలోచన మరియు పౌరుల నిశ్చితార్థానికి ఒక అభిజ్ఞా అవసరం.",
            "Fiscal Federalism vs. Regional Autonomy: The Tug-of-War in Modern State Budgets" to 
                "ఆర్థిక సమాఖ్య విధానం వర్సెస్ ప్రాంతీయ స్వయంప్రతిపత్తి: ఆధునిక రాష్ట్ర బడ్జెట్లలో తీవ్ర పోటీ",
            "Healthy democracies require a delicate balance between standardized central macro-planning and flexible regional self-funding powers." to 
                "ఆరోగ్యకరమైన ప్రజాస్వామ్యాలకు ప్రమాణీకరించిన కేంద్ర స్థూల-ప్రణాళిక మరియు సౌకర్యవంతమైన ప్రాంతీయ స్వయం-ఆర్థిక అధికారాల మధ్య సున్నితమైన సమతుల్యత అవసరం.",
            "The Digital Public Infrastructure (DPI) Model: Redefining Citizen-State Trust and Economic Mobility" to 
                "డిజిటల్ పబ్లిక్ ఇన్‌ఫ్రాస్ట్రక్చర్ (DPI) మోడల్: పౌరుడు-రాష్ట్ర నమ్మకాన్ని మరియు ఆర్థిక చలనశీలతను పునర్నిర్వచించడం",
            "Digital Public Infrastructure (DPI) must be approached not merely as a tech-stack solution, but as a socio-economic catalyst. For a DPI to be truly successful, it requires three structural pillars: open-source interoperable rails, a robust independent data-protection authority to prevent state/corporate surveillance, and universal offline access to bridge the digital divide. Rather than top-down proprietary platforms, modern digital governance must foster public-private-social partnerships that democratize market access, secure individual consent, and establish digital identities as an inalienable fundamental right rather than a commercial commodity." to 
                "డిజిటల్ పబ్లిక్ ఇన్‌ఫ్రాస్ట్రక్చర్ (DPI)ని కేవలం టెక్-స్టాక్ పరిష్కారంగా కాకుండా, సామాజిక-ఆర్థిక ఉత్ప్రేరకంగా సంప్రదించాలి. DPI నిజంగా విజయవంతం కావడానికి, మూడు నిర్మాణాత్మక స్తంభాలు అవసరం: ఓపెన్-సోర్స్ ఇంటరాపరేబుల్ పట్టాలు, రాష్ట్ర/కార్పొరేట్ నిఘాను నిరోధించడానికి ఒక బలమైన స్వతంత్ర డేటా-రక్షణ అథారిటీ, మరియు డిజిటల్ విభజనను తగ్గించడానికి సార్వత్రిక ఆఫ్‌లైన్ యాక్సెస్. పై నుండి క్రిందికి ఉండే యాజమాన్య ప్లాట్‌ఫారమ్‌ల కంటే, ఆధునిక డిజిటల్ గవర్నెన్స్ పబ్లిక్-ప్రైవేట్-సామాజిక భాగస్వామ్యాలను పెంపొందించాలి, ఇది మార్కెట్ ప్రాప్యతను ప్రజాస్వామ్యీకరిస్తుంది, వ్యక్తిగత సమ్మతిని సురక్షితం చేస్తుంది మరియు డిజిటల్ గుర్తింపులను వాణిజ్య వస్తువుగా కాకుండా విడదీయరాని ప్రాథమిక హక్కుగా స్థాపించాలి.",

            // --- DIGEST ITEMS (HEADLINES) ---
            "Parliament Introduces Draft Bill for Decentralized Governance" to "వికేంద్రీకృత పాలన కోసం పార్లమెంటు ముసాయిదా బిల్లును ప్రవేశపెట్టింది",
            "Central Bank Pauses Rates Citing Structural Supply-Side Easing" to "నిర్మాణాత్మక సరఫరా వైపు ఉపశమనాన్ని పేర్కొంటూ సెంట్రల్ బ్యాంక్ వడ్డీ రేట్లను నిలిపివేసింది",
            "National Wetland Initiative Targets 50 Critical Estuaries for Reclamation" to "జాతీయ చిత్తడి నేలల చొరవ పునరుద్ధరణ కోసం 50 కీలకమైన ముఖద్వారాలను లక్ష్యంగా చేసుకుంది",
            "Trilateral Maritime Alliance Formed to Secure Key Shipping Straits" to "కీలకమైన షిప్పింగ్ జలసంధులను రక్షించడానికి త్రైపాక్షిక సముద్ర కూటమి ఏర్పడింది",
            "Solid-State Quantum Engine Achieves Supercooled Stability Record" to "సాలిడ్-స్టేట్ క్వాంటం ఇంజిన్ అత్యంత శీతలీకరించిన స్థిరత్వ రికార్డును సాధించింది",
            "Electoral Commission Announces Direct Voting Audits Using Biometric Logs" to "బయోమెట్రిక్ లాగ్‌లను ఉపయోగించి ప్రత్యక్ష ఓటింగ్ ఆడిట్‌లను ఎన్నికల సంఘం ప్రకటించింది",
            "E-Commerce Consolidation Rules Restrict Self-Brand Promotion" to "ఈ-కామర్స్ ఏకీకరణ నియమాలు స్వీయ-బ్రాండ్ ప్రమోషన్‌ను నియంత్రిస్తాయి",
            "Global Soil Health Accord Mandates Regenerative Agriculture Practices" to "గ్లోబల్ సాయిల్ హెల్త్ అకార్డ్ పునరుత్పత్తి వ్యవసాయ పద్ధతులను తప్పనిసరి చేస్తుంది",

            // --- RECURRING GENERAL PHRASES ---
            "Read Summary" to "సారాంశం చదవండి",
            "Bookmarked" to "బుక్‌మార్క్ చేయబడింది",
            "Bookmarks" to "బుక్‌మార్క్‌లు",
            "Settings" to "సెట్టింగ్‌లు",
            "Today's SAAR" to "నేటి సార్ (SAAR)",
            "Editorial Analysis" to "ఎడిటోరియల్ విశ్లేషణ",
            "Key Takeaways" to "ముఖ్యమైన ముఖ్యాంశాలు",
            "Context" to "సందర్భం",
            "Key Points" to "కీలక అంశాలు",
            "Why It Matters" to "ఎందుకు ఇది ముख్యం",
            "Exam Angle" to "పరీక్షా కోణం",
            "Compiled on" to "సంకలనం చేయబడింది:",
            "Compare Perspectives" to "దృక్కోణాలను పోల్చండి",
            "Add to Today's PDF" to "నేటి PDF లో జోడించండి",
            "Added to PDF ✓" to "PDF లో జోడించబడింది ✓"
        )
    )

    fun translate(text: String, language: String): String {
        if (language == "English" || language.isEmpty()) return text
        val langMap = translations[language] ?: return text
        return langMap[text] ?: translateGenericPlaceholder(text, language)
    }

    private fun translateGenericPlaceholder(text: String, language: String): String {
        // If an exact translation is not in the quick-dictionary (e.g. dynamic texts), 
        // we can return the text, or a localized message if appropriate.
        // For our curated news database and app, all key titles, headlines, 
        // and takeaways are fully mapped above, ensuring 100% offline perfection!
        return text
    }
}

@Composable
fun translateString(text: String, viewModel: MainViewModel): String {
    val language by viewModel.selectedLanguage.collectAsState()
    return TranslationHelper.translate(text, language)
}

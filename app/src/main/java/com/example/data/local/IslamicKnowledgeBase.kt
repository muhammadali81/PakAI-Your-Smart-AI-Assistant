package com.example.data.local

import com.example.data.model.HadithModel
import com.example.data.model.MasnoonDuaModel
import com.example.data.model.QuranAyahModel

object IslamicKnowledgeBase {

    val quranAyahs = listOf(
        QuranAyahModel(
            surahNumber = 1,
            surahNameUrdu = "سورۃ الفاتحہ",
            surahNameEnglish = "Surah Al-Fatihah",
            ayahNumber = 1,
            arabicText = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۝ الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ ۝ الرَّحْمَٰنِ الرَّحِيمِ ۝ مَالِكِ يَوْمِ الدِّينِ ۝ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ۝ اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ ۝ صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ ۝",
            urduTranslation = "اللہ کے نام سے شروع جو نہایت مہربان، بہت رحم فرمانے والا ہے۔ تمام تعریفیں اللہ ہی کے لیے ہیں جو تمام جہانوں کا پروردگار ہے۔ بڑا مہربان نہایت رحم کرنے والا۔ روز جزا کا مالک۔ ہم تیری ہی عبادت کرتے ہیں اور تجھ ہی سے مدد مانگتے ہیں۔ ہمیں سیدھے راستے کی ہدایت فرما، ان لوگوں کے راستے کی جن پر تو نے انعام فرمایا، نہ کہ ان کے جن پر غضب ہوا اور نہ گمراہوں کے۔ (آسان ترجمہ قرآن - مفتی تقی عثمانی)",
            englishTranslation = "In the name of Allah, the Most-Merciful, the Very-Merciful. All praise belongs to Allah, the Lord of all the worlds, the Most-Merciful, the Very-Merciful, Master of the Day of Judgment. You alone do we worship, and from You alone do we seek help. Guide us in the straight path: the path of those whom You have blessed, not of those who incurred Your wrath, nor of those who have gone astray. (Translation by Mufti Muhammad Taqi Usmani)",
            tafseerTaqiUsmani = "مفتی تقی عثمانی صاحب فرماتے ہیں: سورۃ الفاتحہ قرآن کریم کا خلاصہ اور ام الکتاب ہے۔ اس میں پہلے اللہ تعالیٰ کی توحید اور رحمت کا اقرار ہے، پھر قیامت کا یقین، اور پھر خالص بندگی اور دعا کی تلقین ہے۔ 'اہدنا الصراط المستقیم' انسان کی سب سے بڑی اور ضروری دعا ہے جس میں اعتدال اور حق پر قائم رہنے کی التجا کی گئی ہے۔",
            topic = "توحید، عبادت اور صراط مستقیم"
        ),
        QuranAyahModel(
            surahNumber = 2,
            surahNameUrdu = "سورۃ البقرہ (آیت الکرسی)",
            surahNameEnglish = "Surah Al-Baqarah (Ayat al-Kursi)",
            ayahNumber = 255,
            arabicText = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
            urduTranslation = "اللہ وہ ہے جس کے سوا کوئی معبود نہیں، وہ خود زندہ ہے اور ساری کائنات کو تھامنے والا ہے۔ نہ اسے اونگھ آتی ہے نہ نیند۔ اسی کی ملکیت میں ہے جو کچھ آسمانوں میں ہے اور جو کچھ زمین میں ہے۔ کون ہے جو اس کی اجازت کے بغیر اس کے پاس کسی کی سفارش کر سکے؟ وہ سب جانتا ہے جو مخلوق کے سامنے ہے اور جو کچھ ان کے پیچھے ہے۔ اور وہ اس کے علم میں سے کسی چیز پر احاطہ نہیں کر سکتے مگر جتنا وہ خود چاہے۔ اس کی کرسی آسمانوں اور زمین پر چھائی ہوئی ہے، اور ان کی حفاظت اسے ذرا نہیں تھکاتی۔ اور وہ بلند پایہ اور عظیم ہے۔ (آسان ترجمہ قرآن - مفتی تقی عثمانی)",
            englishTranslation = "Allah: there is no god but He, the Living, the All-Sustaining. Neither drowsiness overtakes Him nor sleep. To Him belongs all that is in the heavens and all that is in the earth. Who is there that can intercede with Him except by His leave? He knows what is before them and what is behind them, while they encompass nothing of His knowledge, except what He wills. His Chair encompasses the heavens and the earth, and guarding them does not exhaust Him. And He is the Most High, the Supreme. (Mufti Muhammad Taqi Usmani)",
            tafseerTaqiUsmani = "مفتی تقی عثمانی صاحب کی تفسیر: یہ آیت قرآن کریم کی سب سے عظیم ترین آیت ہے۔ اس میں اللہ تعالیٰ کی دس ایسی صفات بیان ہوئی ہیں جو شرک کی تمام جڑوں کو کاٹ دیتی ہیں۔ 'الحی القیوم' اسم اعظم ہے، اور کائنات کے ہر ذرے پر اللہ کے مکمل اختیار، علم غیب، اور تنہا کبریا ہونے کا بیان ہے۔ ہر فرض نماز کے بعد آیت الکرسی پڑھنا دخول جنت کی ضامن ہے۔",
            topic = "اسم اعظم اور کائنات کی حاکمیت"
        ),
        QuranAyahModel(
            surahNumber = 94,
            surahNameUrdu = "سورۃ الانشراح",
            surahNameEnglish = "Surah Ash-Sharh",
            ayahNumber = 5,
            arabicText = "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا ۝ إِنَّ مَعَ الْعُسْرِ يُسْرًا ۝",
            urduTranslation = "پس حقیقت یہ ہے کہ ہر تنگی کے ساتھ آسانی ہے۔ یقیناً تنگی کے ساتھ آسانی ہے۔ (آسان ترجمہ قرآن)",
            englishTranslation = "So, undoubtedly, along with hardship there is ease. Undoubtedly, along with hardship there is ease. (Mufti Taqi Usmani)",
            tafseerTaqiUsmani = "مفتی تقی عثمانی صاحب لکھتے ہیں: عربی زبان کے قاعدے کے مطابق 'العسر' پر الف لام عہد کا ہے یعنی تنگی ایک ہی ہے جبکہ 'یسراً' نکرہ ہے جو تنوع اور وسعت پر دلالت کرتا ہے یعنی آسانیاں بے شمار ہیں۔ پس ایک تنگی دو آسانیوں پر غالب نہیں آ سکتی۔ یہ آیت ہر پریشان حال مسلمان کے لیے سب سے بڑی ڈھارس ہے۔",
            topic = "امید، صبر اور مشکلات کا حل"
        ),
        QuranAyahModel(
            surahNumber = 3,
            surahNameUrdu = "سورۃ آل عمران",
            surahNameEnglish = "Surah Ali 'Imran",
            ayahNumber = 139,
            arabicText = "وَلَا تَهِنُوا وَلَا تَحْزَنُوا وَأَنتُمُ الْأَعْلَوْنَ إِن كُنتُم مُّؤْمِنِينَ",
            urduTranslation = "اور تم نہ سستی کرو اور نہ غمگین ہو، اور اگر تم مؤمن ہو تو تم ہی غالب رہو گے۔ (آسان ترجمہ قرآن)",
            englishTranslation = "Do not lose heart and do not grieve, for you shall be the upper ones if you are believers. (Mufti Taqi Usmani)",
            tafseerTaqiUsmani = "غزوہ احد کے موقع پر جب مسلمانوں کو عارضی نقصان ہوا تو اللہ تعالیٰ نے یہ تسلی نازل فرمائی کہ ظاہری حالات سے مایوس نہ ہوں، اگر ایمان سچا ہے تو انجام کار فتح اور بلندی تمہاری ہی ہے۔",
            topic = "استقامت اور ایمان کی طاقت"
        ),
        QuranAyahModel(
            surahNumber = 112,
            surahNameUrdu = "سورۃ الاخلاص",
            surahNameEnglish = "Surah Al-Ikhlas",
            ayahNumber = 1,
            arabicText = "قُلْ هُوَ اللَّهُ أَحَدٌ ۝ اللَّهُ الصَّمَدُ ۝ لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ ۝",
            urduTranslation = "کہہ دیجیے کہ وہ اللہ ایک ہے۔ اللہ بے نیاز ہے۔ نہ اس کی کوئی اولاد ہے اور نہ وہ کسی کی اولاد ہے۔ اور نہ کوئی اس کے برابر کا ہے۔ (آسان ترجمہ قرآن)",
            englishTranslation = "Say: He is Allah, the One. Allah, the Self-Sufficient Master. He neither begets nor was He begotten. And there is none comparable unto Him. (Mufti Taqi Usmani)",
            tafseerTaqiUsmani = "یہ سورہ خالص توحید کا بیان ہے۔ لفظ 'الصمد' کا مفہوم یہ ہے کہ تمام مخلوق اپنی ہر ضرورت میں اس کی محتاج ہے اور وہ کسی کا محتاج نہیں۔ حدیث شریف کے مطابق یہ سورت ثلث قرآن (ایک تہائی قرآن) کے برابر ثواب رکھتی ہے۔",
            topic = "خالص توحید اور صفات باری تعالیٰ"
        )
    )

    val authenticHadiths = listOf(
        HadithModel(
            id = 1,
            book = "صحیح البخاری",
            hadithNumber = "حدیث نمبر 1",
            narrator = "حضرت عمر بن الخطاب رضی اللہ عنہ",
            arabicText = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى، فَمَنْ كَانَتْ هِجْرَتُهُ إِلَى دُنْيَا يُصِيبُهَا أَوْ إِلَى امْرَأَةٍ يَنْكِحُهَا فَهِجْرَتُهُ إِلَى مَا هَاجَرَ إِلَيْهِ.",
            urduTranslation = "اعمال کا دارومدار نیتوں پر ہے اور ہر انسان کے لیے وہی ہے جس کی اس نے نیت کی۔ پس جس کی ہجرت دنیا کے حصول کے لیے ہو یا کسی عورت سے نکاح کے لیے ہو، تو اس کی ہجرت اسی کے لیے شمار ہوگی جس کے لیے اس نے ہجرت کی۔",
            englishTranslation = "The reward of deeds depends upon the intentions and every person will get the reward according to what he has intended. So whoever emigrated for worldly benefits or for a woman to marry, his emigration was for what he emigrated for. (Sahih al-Bukhari 1)",
            grading = "صحیح متفق علیہ (Sahih - Agreed Upon)",
            tashreeh = "مفتی تقی عثمانی صاحب کی تشریح: یہ حدیث اسلام کے بنیادی قواعد میں سے ہے اور امام ابو داؤد کے مطابق نصف اسلام ہے۔ ہر نیک عمل تبھی بارگاہ الٰہی میں مقبول ہے جب اس کے پیچھے خلوص نیت اور رضائے الٰہی کا جذبہ ہو۔ اگر ظاہر کتنا ہی بڑا کیوں نہ ہو مگر نیت میں ریاکاری یا دنیا کا لالچ ہو تو وہ عمل ضائع ہو جاتا ہے۔",
            category = "اخلاص اور نیت"
        ),
        HadithModel(
            id = 2,
            book = "صحیح البخاری و صحیح مسلم",
            hadithNumber = "متفق علیہ",
            narrator = "حضرت انس بن مالک رضی اللہ عنہ",
            arabicText = "لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ.",
            urduTranslation = "تم میں سے کوئی شخص اس وقت تک کامل مومن نہیں ہو سکتا جب تک کہ وہ اپنے بھائی کے لیے وہی چیز پسند نہ کرے جو اپنے لیے پسند کرتا ہے۔",
            englishTranslation = "None of you truly believes until he loves for his brother what he loves for himself. (Sahih al-Bukhari 13, Sahih Muslim 45)",
            grading = "صحیح (Sahih)",
            tashreeh = "تشریح: یہ حدیث اسلامی اخوت اور معاشرتی امن کا سنہری اصول ہے۔ کمال ایمان صرف عبادات سے حاصل نہیں ہوتا بلکہ بندوں کے حقوق اور دل کی صفائی سے وابستہ ہے۔ جو خیر، عزت، اور بھلائی ہم اپنے لیے چاہتے ہیں، وہی دوسروں کے لیے چاہنا ایمان کا لازمی تقاضا ہے۔",
            category = "اخلاق اور معاشرت"
        ),
        HadithModel(
            id = 3,
            book = "صحیح مسلم",
            hadithNumber = "حدیث نمبر 223",
            narrator = "حضرت ابو مالک الاشعری رضی اللہ عنہ",
            arabicText = "الطُّهُورُ شَطْرُ الإِيمَانِ، وَالْحَمْدُ لِلَّهِ تَمْلأُ الْمِيزَانَ، وَسُبْحَانَ اللَّهِ وَالْحَمْدُ لِلَّهِ تَمْلآنِ أَوْ تَمْلأُ مَا بَيْنَ السَّمَاوَاتِ وَالأَرْضِ.",
            urduTranslation = "پاکیزگی آدھا ایمان ہے، اور 'الحمد للہ' ترازو کو بھر دیتا ہے، اور 'سبحان اللہ' اور 'الحمد للہ' آسمانوں اور زمین کے درمیان کے فاصلے کو بھر دیتے ہیں۔",
            englishTranslation = "Cleanliness is half of faith, and 'Alhamdulillah' (all praise belongs to Allah) fills the scale, and 'SubhanAllah' and 'Alhamdulillah' fill that which is between heaven and earth. (Sahih Muslim 223)",
            grading = "صحیح (Sahih)",
            tashreeh = "تشریح: طہارت سے مراد ظاہری صفائی (وضو، غسل، پاکیزہ لباس) بھی ہے اور باطنی پاکیزگی (گناہوں اور شرک سے پاکی) بھی۔ اللہ کے ذکر کی فضیلت اتنی عظیم ہے کہ ایک بار دل سے ادا کیا گیا کلمہ قیامت کے دن میزان عمل کو نیکیوں سے بھر دے گا۔",
            category = "طہارت اور ذکر الٰہی"
        ),
        HadithModel(
            id = 4,
            book = "جامع الترمذی",
            hadithNumber = "حدیث نمبر 1987",
            narrator = "حضرت عبداللہ بن عباس رضی اللہ عنہما",
            arabicText = "احْفَظِ اللَّهَ يَحْفَظْكَ، احْفَظِ اللَّهَ تَجِدْهُ تُجَاهَكَ، إِذَا سَأَلْتَ فَاسْأَلِ اللَّهَ، وَإِذَا اسْتَعَنْتَ فَاسْتَعِنْ بِاللَّهِ، وَاعْلَمْ أَنَّ الأُمَّةَ لَوِ اجْتَمَعَتْ عَلَى أَنْ يَنْفَعُوكَ بِشَيْءٍ لَمْ يَنْفَعُوكَ إِلاَّ بِشَيْءٍ قَدْ كَتَبَهُ اللَّهُ لَكَ.",
            urduTranslation = "اللہ کے احکام کی حفاظت کرو، اللہ تمہاری حفاظت فرمائے گا۔ اللہ کے حقوق کا خیال رکھو، تم اسے اپنے سامنے پاؤ گے۔ جب بھی مانگو تو صرف اللہ ہی سے مانگو، اور جب مدد چاہو تو صرف اللہ ہی سے مدد چاہو۔ اور یہ بات جان لو کہ اگر تمام امت مل کر بھی تمہیں کوئی نفع پہنچانا چاہے تو تمہیں اتنا ہی نفع پہنچا سکتی ہے جو اللہ نے تمہارے مقدر میں لکھ دیا ہے۔",
            englishTranslation = "Be mindful of Allah, and He will protect you. Be mindful of Allah, and you will find Him in front of you. If you ask, ask of Allah; if you seek help, seek help from Allah. Know that if the whole community were to gather together to benefit you with something, they could not benefit you except with that which Allah has already written for you. (Jami` at-Tirmidhi)",
            grading = "صحیح حسن (Sahih Hasan)",
            tashreeh = "مفتی تقی عثمانی صاحب اس حدیث کی تشریح میں فرماتے ہیں: یہ توکل علی اللہ اور خودداری کا عظیم ترین سبق ہے۔ جو بندہ اللہ کی شریعت کی پاسداری کرتا ہے، اللہ تعالیٰ دنیا کے حوادث میں اس کا نگہبان بن جاتا ہے اور انسان کو مخلوق کے آگے جھکنے اور ڈرنے سے بے نیاز کر دیتا ہے۔",
            category = "توکل اور دعا"
        ),
        HadithModel(
            id = 5,
            book = "صحیح البخاری",
            hadithNumber = "حدیث نمبر 5027",
            narrator = "حضرت عثمان بن عفان رضی اللہ عنہ",
            arabicText = "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ.",
            urduTranslation = "تم میں سے بہترین شخص وہ ہے جو قرآن سیکھے اور دوسروں کو سکھائے۔",
            englishTranslation = "The best among you are those who learn the Quran and teach it to others. (Sahih al-Bukhari 5027)",
            grading = "صحیح (Sahih)",
            tashreeh = "تشریح: قرآن مجید کلام الٰہی ہے، اس کو درست تلفظ، تجوید اور سمجھ کر پڑھنا اور پھر اس کے احکام اور تعلیمات کو آگے پہنچانا امت کی سب سے بڑی سعادت ہے۔",
            category = "تعلیم قرآن"
        )
    )

    val masnoonDuain = listOf(
        MasnoonDuaModel(
            id = 1,
            titleUrdu = "سید الاستغفار (بخشش کی سب سے بڑی دعا)",
            titleEnglish = "Sayyid al-Istighfar (Chief of Supplications for Forgiveness)",
            occasion = "صبح و شام پڑھنے کے لیے",
            arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ.",
            urduTranslation = "اے اللہ! تو ہی میرا رب ہے، تیرے سوا کوئی معبود نہیں۔ تو نے مجھے پیدا کیا اور میں تیرا بندہ ہوں، اور میں اپنی طاقت کے مطابق تیرے عہد اور وعدے پر قائم ہوں۔ میں اپنے کیے کے شر سے تیری پناہ مانگتا ہوں۔ میں اپنے اوپر تیری نعمتوں کا اعتراف کرتا ہوں، اور اپنے گناہوں کا اقرار کرتا ہوں، پس مجھے بخش دے، کیونکہ تیرے سوا کوئی گناہوں کو معاف نہیں کر سکتا۔",
            englishTranslation = "O Allah, You are my Lord, none has the right to be worshiped but You. You created me and I am Your slave, and I am faithful to my covenant and my promise as much as I can. I seek refuge with You from all the evil I have done. I acknowledge before You all the blessings You have bestowed upon me, and I confess to You all my sins. So I entreat You to forgive my sins, for none can forgive sins except You. (Sahih al-Bukhari)",
            reference = "صحیح البخاری حدیث 6306",
            fazeelat = "حضور اکرم ﷺ نے فرمایا: جو شخص یقین کامل کے ساتھ شام کو یہ دعا پڑھے اور رات کو وفات پا جائے تو وہ جنتی ہے، اور جو صبح پڑھے اور شام سے پہلے وفات پا جائے تو وہ بھی جنتی ہے۔",
            category = "استغفار اور مغفرت"
        ),
        MasnoonDuaModel(
            id = 2,
            titleUrdu = "سفر کی مسنون دعا",
            titleEnglish = "Dua for Travelling (Safar)",
            occasion = "سواری یا سفر شروع کرتے وقت",
            arabicText = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ ۝ وَإِنَّا إِلَىٰ رَبِّنَا لَمُنقَلِبُونَ ۝ اللَّهُمَّ إِنَّا نَسْأَلُكَ فِي سَفَرِنَا هَٰذَا الْبِرَّ وَالتَّقْوَىٰ، وَمِنَ الْعَمَلِ مَا تَرْضَىٰ، اللَّهُمَّ هَوِّنْ عَلَيْنَا سَفَرَنَا هَٰذَا وَاطْوِ عَنَّا بُعْدَهُ.",
            urduTranslation = "پاک ہے وہ ذات جس نے اس (سواری) کو ہمارے قابو میں کر دیا، حالانکہ ہم اسے قابو میں لانے والے نہ تھے۔ اور یقیناً ہم اپنے پروردگار ہی کی طرف لوٹنے والے ہیں۔ اے اللہ! ہم تجھ سے اپنے اس سفر میں نیکی اور پرہیزگاری کا سوال کرتے ہیں، اور ایسے عمل کا جس سے تو راضی ہو۔ اے اللہ! ہمارے لیے اس سفر کو آسان فرما اور اس کی دوری کو سمیٹ دے۔",
            englishTranslation = "Glory to Him Who has subjected this to us, and we could never have it by our efforts. And verily, unto our Lord we indeed are to return. O Allah, we ask You on this journey for goodness and righteousness, and for actions that please You. O Allah, make this journey easy for us and roll up its distance. (Sahih Muslim 1342)",
            reference = "صحیح مسلم حدیث 1342",
            fazeelat = "سفر کے تمام خطرات، پریشانیوں اور حوادث سے مکمل امان اور برکت حاصل ہوتی ہے۔",
            category = "سفر"
        ),
        MasnoonDuaModel(
            id = 3,
            titleUrdu = "غم، پریشانی اور قرض سے نجات کی دعا",
            titleEnglish = "Dua for Relief from Anxiety, Distress & Debt",
            occasion = "پریشانی، بے چینی یا اداسی کے وقت",
            arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَأَعُوذُ بِكَ مِنَ الْعَجْزِ وَالْكَسَلِ، وَأَعُوذُ بِكَ مِنَ الْجُبْنِ وَالْبُخْلِ، وَأَعُوذُ بِكَ مِنْ غَلَبَةِ الدَّيْنِ وَقَهْرِ الرِّجَالِ.",
            urduTranslation = "اے اللہ! میں فکر اور غم سے تیری پناہ مانگتا ہوں، اور عاجزی اور سستی سے تیری پناہ مانگتا ہوں، اور بزدلی اور کنجوسی سے تیری پناہ مانگتا ہوں، اور قرض کے غلبے اور لوگوں کے دباؤ سے تیری پناہ مانگتا ہوں۔",
            englishTranslation = "O Allah, I seek refuge in You from grief and sadness, from weakness and laziness, from miserliness and cowardice, from being overcome by debt and being overpowered by men. (Sahih al-Bukhari 2893)",
            reference = "صحیح البخاری حدیث 2893",
            fazeelat = "حضور اکرم ﷺ کثرت سے یہ دعا مانگتے تھے۔ حضرت ابو امامہ رضی اللہ عنہ فرماتے ہیں کہ میں نے یہ دعا پڑھی تو اللہ تعالیٰ نے میری پریشانی دور کر دی اور سارا قرض ادا فرما دیا۔",
            category = "پریشانی اور سکون قلب"
        ),
        MasnoonDuaModel(
            id = 4,
            titleUrdu = "کھانے سے پہلے اور بعد کی دعائیں",
            titleEnglish = "Dua Before & After Eating",
            occasion = "کھانا کھاتے وقت",
            arabicText = "شروع میں: بِسْمِ اللَّهِ وَعَلَى بَرَكَةِ اللَّهِ ۝\nاگر بھول جائے: بِسْمِ اللَّهِ أَوَّلَهُ وَآخِرَهُ ۝\nکھانے کے بعد: الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنَا وَسَقَانَا وَجَعَلَنَا مِنَ الْمُسْلِمِينَ ۝",
            urduTranslation = "شروع میں: اللہ کے نام کے ساتھ اور اللہ کی برکت پر (کھانا شروع کرتا ہوں)۔\nاگر شروع میں بھول جائے تو: اللہ کے نام کے ساتھ اس کے اول اور آخر میں۔\nکھانے کے بعد: تمام تعریفیں اللہ کے لیے ہیں جس نے ہمیں کھلایا اور پلایا اور ہمیں مسلمانوں میں سے بنایا۔",
            englishTranslation = "Before eating: In the name of Allah and with the blessings of Allah.\nIf forgotten: In the name of Allah at the beginning and at the end.\nAfter eating: All praise is due to Allah Who gave us food and drink and made us Muslims.",
            reference = "جامع الترمذی و سنن ابی داؤد",
            fazeelat = "کھانے میں برکت ہوتی ہے اور شیطان کے شرکت سے حفاظت رہتی ہے۔",
            category = "روزمرہ"
        ),
        MasnoonDuaModel(
            id = 5,
            titleUrdu = "والدین کے لیے قرآنی دعا",
            titleEnglish = "Dua for Parents (From the Holy Quran)",
            occasion = "ہر نماز کے بعد اور دعاؤں میں",
            arabicText = "رَبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا ۝ رَبَّنَا اغْفِرْ لِي وَلِوَالِدَيَّ وَلِلْمُؤْمِنِينَ يَوْمَ يَقُومُ الْحِسَابُ ۝",
            urduTranslation = "اے میرے پروردگار! ان دونوں (میرے والدین) پر رحم فرما جیسا کہ انہوں نے بچپن میں مجھے پالا۔ اے ہمارے پروردگار! مجھے اور میرے والدین کو اور تمام مومنین کو اس دن بخش دے جس دن حساب قائم ہوگا۔ (آسان ترجمہ قرآن)",
            englishTranslation = "My Lord, have mercy upon them as they brought me up when I was small. Our Lord, forgive me and my parents and the believers the Day the account is established. (Quran 17:24, 14:41)",
            reference = "سورۃ الاسراء آیت 24 اور سورۃ ابراہیم آیت 41",
            fazeelat = "والدین کی حیات اور بعد از وفات ان کے درجات کی بلندی کے لیے سب سے بہترین دعا۔",
            category = "قرآنی دعائیں"
        )
    )
}

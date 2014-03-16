package com.jeffsul.riskapp.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;

import com.jeffsul.riskapp.players.Player;

public class Map {
	public HashMap<String,Territory> territs = new HashMap<String,Territory>();
	public HashMap<String,Continent> conts = new HashMap<String,Continent>();
	
	private Player[] players;
	
	public Map(Context ctx, boolean useEpicMap) {
		if (!useEpicMap) {
			Territory ala = new Territory(ctx, "Alaska", 48, 122);
			Territory nwt = new Territory(ctx, "Northwest Territories", 118, 130);
			Territory wca = new Territory(ctx, "Western Canada", 118, 177);
			Territory cca = new Territory(ctx, "Central Canada", 179, 181);
			Territory eca = new Territory(ctx, "Eastern Canada", 236, 179);
			Territory gre = new Territory(ctx, "Greenland", 319, 75);
			Territory wus = new Territory(ctx, "Western United States", 131, 234);
			Territory eus = new Territory(ctx, "Eastern United States", 194, 248);
			Territory cam = new Territory(ctx, "Central America", 156, 304);
			Territory[] namTerrits = {ala,nwt,wca,cca,eca,gre,wus,eus,cam};
			Continent nam = new Continent("North America", 5, namTerrits, new Territory[] {gre,ala,cam});
			for (Territory t : namTerrits) {
				territs.put(t.name, t);
			}
			conts.put(nam.name, nam);
			
			Territory ven = new Territory(ctx, "Venezuala", 227, 374);
			Territory per = new Territory(ctx, "Peru", 228, 440);
			Territory arg = new Territory(ctx, "Argentina", 242, 507);
			Territory bra = new Territory(ctx, "Brazil", 280, 421);
			Territory[] samTerrits = {ven,per,arg,bra};
			Continent sam = new Continent("South America", 2, samTerrits, new Territory[] {ven,bra});
			for (Territory t : samTerrits) {
				territs.put(t.name, t);
			}
			conts.put(sam.name, sam);
			
			Territory naf = new Territory(ctx, "North Africa", 400, 339);
			Territory con = new Territory(ctx, "Congo", 462, 408);
			Territory saf = new Territory(ctx, "South Africa", 472, 485);
			Territory mad = new Territory(ctx, "Madagascar", 544, 469);
			Territory eaf = new Territory(ctx, "East Africa", 507, 379);
			Territory egy = new Territory(ctx, "Egypt", 465, 311);
			Territory[] afrTerrits = {naf,con,saf,mad,eaf,egy};
			Continent afr = new Continent("Africa", 3, afrTerrits, new Territory[] {naf,egy,eaf});
			for (Territory t : afrTerrits) {
				territs.put(t.name, t);
			}
			conts.put(afr.name, afr);
			
			Territory ice = new Territory(ctx, "Iceland", 377, 129);
			Territory sca = new Territory(ctx, "Scandinavia", 438, 145);
			Territory gbr = new Territory(ctx, "Great Britain", 392, 194);
			Territory weu = new Territory(ctx, "Western Europe", 391, 246);
			Territory neu = new Territory(ctx, "Northern Europe", 440, 201);
			Territory seu = new Territory(ctx, "Southern Europe", 461, 235);
			Territory ukr = new Territory(ctx, "Ukraine", 501, 178);
			Territory[] eurTerrits = {ice,sca,gbr,weu,neu,seu,ukr};
			Continent eur = new Continent("Europe", 5, eurTerrits, new Territory[] {ice,ukr,seu,weu});
			for (Territory t : eurTerrits) {
				territs.put(t.name, t);
			}
			conts.put(eur.name, eur);
			
			Territory ino = new Territory(ctx, "Indonesia", 688, 400);
			Territory ngu = new Territory(ctx, "New Guinea", 770, 416);
			Territory wau = new Territory(ctx, "Western Australia", 691, 514);
			Territory eau = new Territory(ctx, "Eastern Australia", 759, 530);
			Territory[] oceTerrits = {ino,ngu,wau,eau};
			Continent oce = new Continent("Oceania", 2, oceTerrits, new Territory[] {ino});
			for (Territory t : oceTerrits) {
				territs.put(t.name, t);
			}
			conts.put(oce.name, oce);
			
			Territory mea = new Territory(ctx, "Middle East", 524, 293);
			Territory afg = new Territory(ctx, "Afghanistan", 573, 241);
			Territory ind = new Territory(ctx, "India", 600, 312);
			Territory sia = new Territory(ctx, "Siam", 660, 327);
			Territory chi = new Territory(ctx, "China", 656, 270);
			Territory ura = new Territory(ctx, "Ural", 577, 170);
			Territory sib = new Territory(ctx, "Siberia", 631, 141);
			Territory mon = new Territory(ctx, "Mongolia", 666, 218);
			Territory jap = new Territory(ctx, "Japan", 755, 258);
			Territory irk = new Territory(ctx, "Irkutsk", 690, 169);
			Territory yak = new Territory(ctx, "Yakutsk", 735, 124);
			Territory kam = new Territory(ctx, "Kamchatka", 809, 123);
			Territory[] asiTerrits = {mea,afg,ind,sia,chi,ura,sib,mon,jap,irk,yak,kam};
			Continent asi = new Continent("Asia", 7, asiTerrits, new Territory[] {mea,afg,sia,kam,sib});
			for (Territory t : asiTerrits) {
				territs.put(t.name, t);
			}
			conts.put(asi.name, asi);
			
			ala.connect(new Territory[] {kam,nwt,wca});
			nwt.connect(new Territory[] {ala,wca,cca,gre});
			gre.connect(new Territory[] {nwt,cca,eca,ice});
			wca.connect(new Territory[] {ala,nwt,cca,wus});
			cca.connect(new Territory[] {nwt,wca,wus,eus,eca,gre});
			eca.connect(new Territory[] {gre,cca,eus});
			wus.connect(new Territory[] {wca,cca,eus,cam});
			eus.connect(new Territory[] {cca,eca,wus,cam});
			cam.connect(new Territory[] {wus,eus,ven});
			
			ven.connect(new Territory[] {cam,per,bra});
			per.connect(new Territory[] {ven,bra,arg});
			arg.connect(new Territory[] {per,bra});
			bra.connect(new Territory[] {ven,per,arg,naf});
			
			naf.connect(new Territory[] {bra,con,eaf,egy,weu,seu});
			con.connect(new Territory[] {naf,eaf,saf});
			saf.connect(new Territory[] {con,eaf,mad});
			mad.connect(new Territory[] {saf,eaf});
			eaf.connect(new Territory[] {mad,saf,con,naf,egy,mea});
			egy.connect(new Territory[] {naf,eaf,mea,seu});
			
			weu.connect(new Territory[] {naf,gbr,seu,neu});
			seu.connect(new Territory[] {weu,neu,egy,naf,mea,ukr});
			neu.connect(new Territory[] {seu,weu,gbr,ukr,sca});
			ice.connect(new Territory[] {gre,gbr,sca});
			gbr.connect(new Territory[] {ice,weu,neu,sca});
			ice.connect(new Territory[] {gre,gbr,sca});
			sca.connect(new Territory[] {ice,ukr,gbr,neu});
			ukr.connect(new Territory[] {sca,neu,seu,mea,afg,ura});
			
			mea.connect(new Territory[] {eaf,egy,ukr,seu,ind,afg});
			afg.connect(new Territory[] {mea,ukr,ind,chi,ura});
			ura.connect(new Territory[] {ukr,afg,chi,sib});
			sib.connect(new Territory[] {chi,ura,mon,irk,yak});
			ind.connect(new Territory[] {mea,afg,chi,sia});
			sia.connect(new Territory[] {ind,chi,ino});
			chi.connect(new Territory[] {sia,ind,afg,ura,sib,mon});
			mon.connect(new Territory[] {chi,sib,jap,kam,irk});
			jap.connect(new Territory[] {mon,kam});
			irk.connect(new Territory[] {mon,sib,yak,kam});
			yak.connect(new Territory[] {kam,sib,irk});
			kam.connect(new Territory[] {ala,jap,mon,irk,yak});
			
			ino.connect(new Territory[] {sia,wau,ngu});
			ngu.connect(new Territory[] {ino,wau,eau});
			wau.connect(new Territory[] {ino,eau,ngu});
			eau.connect(new Territory[] {wau,ngu});
		} else {
			Territory ala = new Territory(ctx, "Alaska", 67, 93);
			Territory yuk = new Territory(ctx, "Yukon", 133, 103);
			Territory nun = new Territory(ctx, "Nunavut", 207, 85);
			Territory bc = new Territory(ctx, "B.C.", 91, 132);
			Territory pra = new Territory(ctx, "Prairies", 147, 132);
			Territory upcan = new Territory(ctx, "Upper Canada", 197, 164);
			Territory locan = new Territory(ctx, "Lower Canada", 248, 129);
			Territory gre = new Territory(ctx, "Greenland", 354, 65);
			
			Territory westus = new Territory(ctx, "Western USA", 88, 177);
			Territory midus = new Territory(ctx, "Midwest USA", 142, 175);
			Territory southus = new Territory(ctx, "Southern USA", 186, 217);
			Territory neweng = new Territory(ctx, "New England", 216, 191);
			
			Territory mex = new Territory(ctx, "Mexico", 102, 271);
			Territory guat = new Territory(ctx, "Guatamala", 142, 290);
			Territory pan = new Territory(ctx, "Panama", 163, 311);
			Territory cuba = new Territory(ctx, "Cuba", 180, 267);
			Territory carib = new Territory(ctx, "Caribbean", 215, 279);
			
			Territory col = new Territory(ctx, "Colombia", 190, 338);
			Territory ven = new Territory(ctx, "Venezuala", 223, 326);
			Territory guy = new Territory(ctx, "Guyanas", 250, 335);
			Territory ecua = new Territory(ctx, "Ecuador", 162, 358);
			Territory peru = new Territory(ctx, "Peru", 186, 391);
			Territory bol = new Territory(ctx, "Bolivia", 223, 421);
			Territory amaz = new Territory(ctx, "Amazonas", 243, 365);
			Territory nebra = new Territory(ctx, "North East Brazil", 300, 386);
			Territory paran = new Territory(ctx, "Parana", 269, 419);
			Territory para = new Territory(ctx, "Paraguay", 246, 444);
			Territory uru = new Territory(ctx, "Uruguay", 266, 481);
			Territory arg = new Territory(ctx, "Argentina", 236, 484);
			Territory chile = new Territory(ctx, "Chile", 203, 502);
			Territory tier = new Territory(ctx, "Tierra del Fuego", 259, 568);
			
			Territory chclaim = new Territory(ctx, "Chilean Claim", 264, 630);
			territs.put("Chilean Claim", chclaim);
			Territory britclaim = new Territory(ctx, "British Claim", 405, 639);
			territs.put("British Claim", britclaim);
			Territory norclaim = new Territory(ctx, "Norwegian Claim", 566, 632);
			territs.put("Norwegian Claim", norclaim);
			Territory austclaim = new Territory(ctx, "Australian Claim", 708, 630);
			territs.put("Australian Claim", austclaim);
			
			Territory ice = new Territory(ctx, "Iceland", 364, 115);
			Territory nor = new Territory(ctx, "Norway", 406, 115);
			Territory swe = new Territory(ctx, "Sweden", 437, 115);
			Territory fin = new Territory(ctx, "Finland", 460, 95);
			Territory brit = new Territory(ctx, "British Isles", 373, 147);
			Territory fra = new Territory(ctx, "France", 388, 175);
			Territory ibe = new Territory(ctx, "Iberia", 368, 199);
			Territory ita = new Territory(ctx, "Italy", 428, 196);
			Territory ger = new Territory(ctx, "Germany", 415, 158);
			Territory pol = new Territory(ctx, "Poland", 439, 158);
			Territory balt = new Territory(ctx, "Baltics", 458, 137);
			Territory ukr = new Territory(ctx, "Ukraine", 469, 168);
			Territory mosk = new Territory(ctx, "Moskva", 507, 142);
			Territory grc = new Territory(ctx, "Greece", 455, 202);
			
			Territory mor = new Territory(ctx, "Morocco", 343, 246);
			Territory maur = new Territory(ctx, "Mauritania", 348, 280);
			Territory alg = new Territory(ctx, "Algeria", 397, 235);
			Territory mali = new Territory(ctx, "Mali", 376, 284);
			Territory sen = new Territory(ctx, "Senegal", 331, 302);
			Territory guin = new Territory(ctx, "Guineas", 344, 321);
			Territory cote = new Territory(ctx, "Cote D'Ivoire", 375, 327);
			Territory nig = new Territory(ctx, "Nigeria", 411, 315);
			Territory niger = new Territory(ctx, "Niger", 421, 280);
			Territory lib = new Territory(ctx, "Libya", 426, 250);
			Territory egy = new Territory(ctx, "Egypt", 479, 245);
			Territory chad = new Territory(ctx, "Chad", 445, 288);
			Territory cam = new Territory(ctx, "Cameroon", 427, 349);
			Territory sud = new Territory(ctx, "Sudan", 484, 287);
			Territory eth = new Territory(ctx, "Ethiopia", 512, 315);
			Territory som = new Territory(ctx, "Somalia", 553, 325);
			Territory ken = new Territory(ctx, "Kenya", 499, 351);
			Territory con = new Territory(ctx, "Congo", 461, 357);
			Territory tan = new Territory(ctx, "Tanzania", 498, 378);
			Territory mad = new Territory(ctx, "Madagascar", 544, 419);
			Territory moz = new Territory(ctx, "Mozambique", 502, 417);
			Territory ang = new Territory(ctx, "Angola", 444, 401);
			Territory zim = new Territory(ctx, "Zimbabwe", 472, 419);
			Territory nam = new Territory(ctx, "Namibia", 438, 440);
			Territory safr = new Territory(ctx, "South Africa", 462, 473);
			
			Territory yem = new Territory(ctx, "Yemen", 542, 294);
			Territory oma = new Territory(ctx, "Oman", 571, 268);
			Territory saud = new Territory(ctx, "Saudi", 531, 259);
			Territory iraq = new Territory(ctx, "Iraq", 523, 227);
			Territory lev = new Territory(ctx, "Levant", 496, 224);
			Territory turk = new Territory(ctx, "Turkey", 483, 203);
			Territory iran = new Territory(ctx, "Iran", 563, 222);
			Territory afg = new Territory(ctx, "Afghanistan", 592, 224);
			Territory turkm = new Territory(ctx, "Turkmenistan", 568, 195);
			Territory kaz = new Territory(ctx, "Kazakhstan", 584, 159);
			Territory kom = new Territory(ctx, "Komi", 532, 117);
			Territory yug = new Territory(ctx, "Yugra", 601, 94);
			Territory eve = new Territory(ctx, "Evenkia", 669, 103);
			Territory sak = new Territory(ctx, "Sakha", 752, 101);
			Territory irk = new Territory(ctx, "Irkutsk", 765, 150);
			Territory mon = new Territory(ctx, "Mongolia", 712, 176);
			Territory china = new Territory(ctx, "China", 724, 212);
			Territory kor = new Territory(ctx, "Korea", 784, 212);
			Territory jap = new Territory(ctx, "Japan", 823, 215);
			Territory tai = new Territory(ctx, "Taiwan", 796, 265);
			Territory pak = new Territory(ctx, "Pakistan", 612, 235);
			Territory ind = new Territory(ctx, "India", 640, 267);
			Territory sri = new Territory(ctx, "Sri Lanka", 664, 330);
			Territory nep = new Territory(ctx, "Nepal", 652, 243);
			Territory thai = new Territory(ctx, "Thailand", 701, 276);
			Territory indo = new Territory(ctx, "Indochina", 742, 307);
			
			Territory haw = new Territory(ctx, "Hawaii", 889, 269);
			Territory phil = new Territory(ctx, "Philippines", 795, 312);
			Territory bor = new Territory(ctx, "Borneo", 763, 347);
			Territory sum = new Territory(ctx, "Sumatra", 725, 367);
			Territory java = new Territory(ctx, "Java", 753, 386);
			Territory sula = new Territory(ctx, "Sulawesi", 795, 368);
			Territory irian = new Territory(ctx, "Irian Jaya", 841, 372);
			Territory papua = new Territory(ctx, "Papua New Guinea", 872, 381);
			Territory newcal = new Territory(ctx, "New Caledonia", 916, 436);
			Territory newzeal = new Territory(ctx, "New Zealand", 909, 501);
			Territory eaust = new Territory(ctx, "Eastern Australia", 865, 469);
			Territory caust = new Territory(ctx, "Central Australia", 821, 429);
			Territory waust = new Territory(ctx, "Western Australia", 784, 445);
			
			Territory[] cont1 = {yuk,nun,bc,pra,upcan,locan};
			conts.put("Canada", new Continent("Canada", 4, cont1, cont1));
			Territory[] cont2 = {westus,midus,southus,neweng};
			conts.put("United States", new Continent("United States", 3, cont2, cont2));
			Territory[] cont3 = {mex,guat,pan};
			conts.put("Central America", new Continent("Central America", 2, cont3, new Territory[] {mex,pan}));
			Territory[] cont4 = {ala,yuk,nun,gre,locan,upcan,pra,bc,westus,midus,neweng,southus,mex,cuba,guat,carib,pan};
			for (Territory t : cont4) {
				territs.put(t.name, t);
			}
			conts.put("North America", new Continent("North America", 2, cont4, new Territory[] {mex,pan,gre,ala}));
			
			Territory[] cont5 = {col,ven,ecua,peru,amaz,bol};
			conts.put("Amazon", new Continent("Amazon", 4, cont5, new Territory[] {col,ven,amaz,bol,peru}));
			Territory[] cont6 = {paran,para,uru,arg};
			conts.put("La Plata", new Continent("La Plata", 3, cont6, new Territory[] {arg,para,paran}));
			Territory[] cont7 = {col,ven,guy,ecua,peru,amaz,bol,paran,para,uru,arg,tier,chile,nebra};
			for (Territory t : cont7) {
				territs.put(t.name, t);
			}
			conts.put("South America", new Continent("South America", 2, cont7, new Territory[] {tier,nebra,col}));
			
			Territory[] cont8 = {nor,swe,fin};
			conts.put("Scandinavia", new Continent("Scandinavia", 2, cont8, new Territory[] {fin,nor}));
			Territory[] cont9 = {ibe,fra,brit,ger,ita,pol};
			conts.put("Western Europe", new Continent("Western Europe", 3, cont9, new Territory[] {pol,brit,ibe}));
			Territory[] cont10 = {nor,swe,fin,ibe,fra,brit,ger,ita,pol,ice,grc,ukr,balt,mosk};
			for (Territory t : cont10) {
				territs.put(t.name, t);
			}
			conts.put("Europe", new Continent("Europe", 6, cont10, new Territory[] {ice,mosk,grc,ibe}));
			
			Territory[] cont11 = {mor,alg,maur,mali,niger,nig,cote,sen,guin};
			conts.put("Mahgreb", new Continent("Mahgreb", 5, cont11, new Territory[] {guin,mor,alg,nig,niger}));
			Territory[] cont12 = {con,tan,moz,zim,ang,nam,safr};
			conts.put("Southern Africa", new Continent("Southern Africa", 4, cont12, new Territory[] {safr,moz,tan,con}));
			Territory[] cont13 = {sud,eth,som,ken};
			conts.put("The Horn", new Continent("The Horn", 3, cont13, new Territory[] {ken,som,sud}));
			Territory[] cont14 = {mor,alg,maur,mali,niger,nig,cote,sen,guin,con,tan,mad,moz,zim,ang,nam,safr,sud,eth,som,ken,cam,chad,lib,egy};
			for (Territory t : cont14) {
				territs.put(t.name, t);
			}
			conts.put("Africa", new Continent("Africa", 3, cont14, new Territory[] {safr,som,egy,mor,guin}));
			
			Territory[] cont15 = {yem,oma,saud,iraq,lev,turk,iran};
			conts.put("Middle East", new Continent("Middle East", 4, cont15, new Territory[] {yem,iran,lev,turk}));
			Territory[] cont16 = {kom,yug,eve,irk,sak};
			conts.put("Russia", new Continent("Russia", 4, cont16, cont16));
			Territory[] cont17 = {nep,pak,ind,sri};
			conts.put("Indian Subcontinent", new Continent("Indian Subcontinent", 3, cont17, new Territory[] {ind,nep,pak}));
			Territory[] cont18 = {mon,kor,jap,tai,china};
			conts.put("Far East", new Continent("Far East", 4, cont18, new Territory[] {tai,china,mon}));
			Territory[] cont19 = {yem,oma,saud,iraq,lev,turk,iran,kom,yug,eve,irk,sak,nep,pak,ind,sri,mon,kor,jap,tai,china,kaz,turkm,afg,thai,indo};
			for (Territory t : cont19) {
				territs.put(t.name, t);
			}
			conts.put("Asia", new Continent("Asia", 3, cont19, new Territory[] {yem,lev,turk,iran,kaz,kom,thai,tai,sak}));
			
			Territory[] cont20 = {waust,eaust,caust};
			conts.put("Australia", new Continent("Australia", 2, cont20, new Territory[] {eaust,waust}));
			Territory[] cont21 = {phil,bor,sum,sula,irian,java};
			conts.put("East Indies", new Continent("East Indies", 4, cont21, new Territory[] {irian,java,sum,phil}));
			Territory[] cont22 = {haw,phil,bor,sum,java,sula,irian,papua,newcal,newzeal,eaust,caust,waust};
			for (Territory t : cont22) {
				territs.put(t.name, t);
			}
			conts.put("Oceania", new Continent("Oceania", 3, cont22, new Territory[] {phil,haw,sum,waust}));
			
			Territory[] cont23 = {chclaim,norclaim,austclaim,britclaim};
			conts.put("Antarctica", new Continent("Antarctica", 0, cont23, cont23));
			
			ala.connect(new Territory[] {yuk,bc,sak});
			yuk.connect(new Territory[] {ala,bc,pra,nun});
			nun.connect(new Territory[] {yuk,pra,locan,gre});
			bc.connect(new Territory[] {ala,yuk,pra,westus});
			pra.connect(new Territory[] {yuk,bc,nun,upcan,midus,westus});
			upcan.connect(new Territory[] {pra,locan,neweng,midus});
			locan.connect(new Territory[] {gre,nun,upcan,neweng});
			gre.connect(new Territory[] {nun,locan,ice});
			
			westus.connect(new Territory[] {bc,pra,midus,southus,mex});
			midus.connect(new Territory[] {westus,pra,upcan,neweng,southus});
			southus.connect(new Territory[] {cuba,mex,neweng,midus,westus});
			neweng.connect(new Territory[] {locan,upcan,southus,midus});
			mex.connect(new Territory[] {westus,southus,cuba,guat,haw});
			guat.connect(new Territory[] {mex,pan});
			pan.connect(new Territory[] {guat,col});
			cuba.connect(new Territory[] {carib,mex,southus});
			carib.connect(new Territory[] {cuba});
			
			chclaim.connect(new Territory[] {austclaim,britclaim,tier});
			britclaim.connect(new Territory[] {chclaim,norclaim,tier});
			norclaim.connect(new Territory[] {britclaim,austclaim,safr});
			austclaim.connect(new Territory[] {norclaim,chclaim,waust});
			
			col.connect(new Territory[] {pan,ecua,ven,amaz,peru});
			ven.connect(new Territory[] {col,guy,amaz});
			guy.connect(new Territory[] {ven,amaz});
			ecua.connect(new Territory[] {peru,col});
			peru.connect(new Territory[] {ecua,col,amaz,bol,chile});
			bol.connect(new Territory[] {peru,amaz,paran,para,chile,arg});
			amaz.connect(new Territory[] {guy,ven,col,peru,bol,paran,nebra});
			nebra.connect(new Territory[] {guin,amaz,paran});
			paran.connect(new Territory[] {nebra,amaz,bol,para,uru,arg});
			para.connect(new Territory[] {bol,paran,arg});
			uru.connect(new Territory[] {arg,paran});
			arg.connect(new Territory[] {chile,uru,paran,para,bol,tier});
			chile.connect(new Territory[] {peru,bol,arg,tier});
			tier.connect(new Territory[] {chile,arg,chclaim,britclaim});
			
			ice.connect(new Territory[] {gre,brit,nor});
			nor.connect(new Territory[] {ice,swe,fin});
			swe.connect(new Territory[] {nor,fin});
			fin.connect(new Territory[] {nor,swe,mosk});
			brit.connect(new Territory[] {ice,fra});
			fra.connect(new Territory[] {ibe,brit,ger,ita});
			ibe.connect(new Territory[] {mor,fra});
			ita.connect(new Territory[] {fra,ger,pol});
			ger.connect(new Territory[] {fra,ita,pol});
			pol.connect(new Territory[] {ita,ger,balt,ukr,grc});
			balt.connect(new Territory[] {pol,ukr,mosk});
			ukr.connect(new Territory[] {pol,balt,mosk,grc});
			mosk.connect(new Territory[] {fin,balt,ukr,turk,iran,kom,kaz});
			grc.connect(new Territory[] {pol,ukr,turk});
			
			mor.connect(new Territory[] {ibe,alg,maur});
			maur.connect(new Territory[] {sen,mor,alg,mali});
			alg.connect(new Territory[] {mor,niger,mali,lib,maur});
			mali.connect(new Territory[] {alg,niger,cote,guin,sen,maur});
			sen.connect(new Territory[] {maur,mali,guin});
			guin.connect(new Territory[] {nebra,cote,sen,mali});
			cote.connect(new Territory[] {guin,mali,nig,niger});
			nig.connect(new Territory[] {cote,niger,chad,cam});
			niger.connect(new Territory[] {alg,mali,cote,nig,chad,lib});
			lib.connect(new Territory[] {alg,niger,chad,egy,sud});
			egy.connect(new Territory[] {lib,sud,lev});
			chad.connect(new Territory[] {lib,niger,nig,cam,sud});
			cam.connect(new Territory[] {nig,chad,sud,con});
			sud.connect(new Territory[] {egy,lib,chad,cam,eth,ken,con});
			eth.connect(new Territory[] {sud,ken,som});
			ken.connect(new Territory[] {sud,eth,som,con,tan});
			con.connect(new Territory[] {cam,sud,ken,tan,ang,zim});
			tan.connect(new Territory[] {ken,con,zim,moz});
			mad.connect(new Territory[] {moz});
			moz.connect(new Territory[] {tan,mad,safr,zim});
			ang.connect(new Territory[] {con,zim,nam});
			zim.connect(new Territory[] {con,tan,moz,ang,nam,safr});
			nam.connect(new Territory[] {ang,zim,safr});
			safr.connect(new Territory[] {nam,zim,moz,norclaim});
			som.connect(new Territory[] {ken,eth,yem});
			
			yem.connect(new Territory[] {som,oma,saud});
			oma.connect(new Territory[] {saud,yem});
			saud.connect(new Territory[] {oma,yem,iraq,lev});
			iraq.connect(new Territory[] {saud,iran,turk,lev});
			lev.connect(new Territory[] {egy,turk,iraq,saud});
			turk.connect(new Territory[] {grc,mosk,lev,iraq,iran});
			iran.connect(new Territory[] {iraq,turk,mosk,turkm,afg,pak});
			afg.connect(new Territory[] {pak,iran,turkm});
			turkm.connect(new Territory[] {kaz,iran,afg,pak,china});
			kaz.connect(new Territory[] {kom,yug,eve,china,turkm,mosk});
			kom.connect(new Territory[] {mosk,kaz,yug});
			yug.connect(new Territory[] {kom,kaz,eve});
			eve.connect(new Territory[] {yug,kaz,mon,sak,irk,china});
			sak.connect(new Territory[] {ala,irk,eve});
			irk.connect(new Territory[] {sak,eve,mon,china});
			mon.connect(new Territory[] {eve,irk,china});
			china.connect(new Territory[] {eve,mon,irk,kor,indo,thai,ind,nep,pak,turkm,kaz,tai});
			kor.connect(new Territory[] {china,jap});
			jap.connect(new Territory[] {kor,tai});
			tai.connect(new Territory[] {jap,china,phil,haw});
			pak.connect(new Territory[] {iran,afg,china,ind,turkm});
			ind.connect(new Territory[] {sri,nep,pak,thai,china});
			sri.connect(new Territory[] {ind});
			nep.connect(new Territory[] {china,ind});
			thai.connect(new Territory[] {ind,china,indo,sum});
			indo.connect(new Territory[] {thai,china});
			
			haw.connect(new Territory[] {mex,tai,phil});
			phil.connect(new Territory[] {haw,tai,bor});
			bor.connect(new Territory[] {sum,sula,phil});
			sum.connect(new Territory[] {thai,bor,java});
			java.connect(new Territory[] {sum,waust});
			sula.connect(new Territory[] {bor,irian});
			irian.connect(new Territory[] {sula,papua});
			papua.connect(new Territory[] {irian,newcal,eaust});
			newcal.connect(new Territory[] {newzeal,eaust,papua});
			newzeal.connect(new Territory[] {newcal,eaust});
			eaust.connect(new Territory[] {papua,newcal,newzeal,caust});
			caust.connect(new Territory[] {waust,eaust});
			waust.connect(new Territory[] {java,caust,austclaim});
		}
	}

	/**
	 * Return the Territory with the given name.
	 */
	public Territory getTerritory(String name) {
		return territs.get(name);
	}

	/**
	 * Return the Continent with the given name.
	 */
	public Continent getContinent(String name) {
		return conts.get(name);
	}

	/**
	 * Get the smallest Continent containing the given Territory.
	 */
	public Continent getContinent(Territory t) {
		// TODO(jeffsul): Cache this with HashMap.
		Continent c = null;
		Collection<Continent> cont = conts.values();
		Iterator<Continent> itr = cont.iterator();
		Continent n;
		while (itr.hasNext()) {
			n = itr.next();
			if (n.hasTerritory(t)) {
				if (c == null)
					c = n;
				else if (n.getSize() < c.getSize())
					c = n;
			}
		}
		return c;
	}
	
	public Continent[] getContinents() {
		Continent[] c = new Continent[conts.size()];
		return new ArrayList<Continent>(conts.values()).toArray(c);
	}
	
	public int getTerritoryCount(Player p) {
		ArrayList<Territory> territs = getTerritories();
		int count = 0;
		for (Territory t : territs)
		{
			if (t.owner == p)
				count++;
		}
		return count;
	}
	
	public int getTroopCount(Player p) {
		ArrayList<Territory> territs = getTerritories();
		int count = 0;
		for (Territory t : territs) {
			if (t.owner == p)
				count += t.units;
		}
		return count;
	}
	
	public ArrayList<Territory> getTerritories() {
		Collection<Territory> t = territs.values();
		ArrayList<Territory> tt = new ArrayList<Territory>();
		tt.addAll(t);
		return tt;
	}
	
	public Territory[] getTerritories(Player p) {
		Collection<Territory> t = territs.values();
		ArrayList<Territory> tt = new ArrayList<Territory>();
		ArrayList<Territory> ttt = new ArrayList<Territory>();
		tt.addAll(t);
		for (int i = 0; i < tt.size(); i++)
			if (tt.get(i).owner == p)
				ttt.add(tt.get(i));
		Territory[] terrs = new Territory[ttt.size()];
		return ttt.toArray(terrs);
	}
	
	public void setPlayers(Player[] players) {
		this.players = players;
	}

	public Player[] getPlayers() {
		return players;
	}
}


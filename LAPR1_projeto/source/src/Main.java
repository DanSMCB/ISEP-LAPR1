import org.la4j.Matrix;
import org.la4j.linear.GaussianSolver;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    public static String path ="C:\\Users\\danie\\OneDrive\\Documents\\LAPR1_projeto\\source\\out\\artifacts\\LAPR1_TurmaDIJ_Grupo05_jar";
    static Scanner ler = new Scanner(System.in);
    public static void main(String[] args) throws IOException {
        ArgumentDetector(args);
    }
    public static void out(String str){
        System.out.println(str);
    }
    public static void ArgumentDetector(String[] args) throws IOException {

        if (args.length == 0){ //Interativo sem fich de entrada
            out("Menu Interativo");
            System.out.println("Nome da espécie: ");
            String nomeespecie= ler.nextLine();
            System.out.println("Digite a quantidade de faixas etárias para a análise da espécie:");
            int N = ler.nextInt();
            int[] vetor_X = vetor_X(N);
            double[][] matriz = matriz(N);

            menu(vetor_X, matriz, nomeespecie);
        }
        else if (args.length == 2 && !getSingleValueArgs('n', args).equals("")){ //Interativo com fich de entrada
            out("Menu Interativo com ficheiro");
            String v = getSingleValueArgs('n', args);

            if (v.equals("")) out("Argumento inexistente");
            else{
                try {
                    path = v;
                    v= v.replace(".txt", "");
                    char[] char_X = read_char_X();
                    char[] char_S = read_char_S();
                    char[] char_F = read_char_F();
                    boolean bool=verification(char_X,char_S,char_F);
                    if(bool){
                        int[] X = fill_X(char_X);
                        double[][] Matriz = fill_S_F(char_S,char_F);
                        menu(X, Matriz, v);
                    }
                    else
                    {
                        System.out.println("Erro na sequência de dados introduzida no ficheiro.");
                    }
                } catch (FileNotFoundException e){
                    out("Erro: Ficheiro não encontrado");
                }
            }
        }
        else{ // Modo N. Interativo
            //Opções Obrigatorias
            out("Menu N/Interativo");


            String t;
            String g;
            String[] inOut;

            t = getSingleValueArgs('t', args);
            g = getSingleValueArgs('g', args);
            inOut = getLastTwoArgs(args);

            int ciclos = -1;
            try {
                ciclos = Integer.parseInt(t);
            }
            catch(Exception ignored){}

            path = inOut[0];

            char[] char_X = read_char_X();
            char[] char_S = read_char_S();
            char[] char_F = read_char_F();
            boolean bool=verification(char_X,char_S,char_F);
            if(bool){
                int[] X = fill_X(char_X);
                double[][] Matriz = fill_S_F(char_S,char_F);
                if ( ciclos==-1 ||t.equals("") || g.equals("") || !(g.equals("1") || g.equals("2") || g.equals("3"))){
                    out("Erro nos Args -t ou -g");
                }
                else if (inOut[0].equals("") || inOut[1].equals("")){
                    out("Falta ficheiro de entrada e saida");
                }
                else {
                    out("Ciclos " + t);

                    String formato;
                    if(g.equals("1")){
                        formato="png";
                    }else if(g.equals("2")){
                        formato="txt";
                    }else formato="eps";

                    out("Formato Gráfico " + formato);
                    boolean arge = false, argv=false, argr=false;
                    char [] optRef = {'e', 'v', 'r'};
                    for (char c : optRef){
                        if (getBoolValueArgs(c, args)){
                            switch (c){
                                case 'e':
                                    arge=true;
                                    break;
                                case 'v':
                                    argv=true;
                                    break;
                                case 'r':
                                    argr=true;
                                    break;
                            }
                        }
                    }
                    String nomeespecie= inOut[0];
                    nomeespecie= nomeespecie.replace(".txt", "");
                    modo_nao_interativo(Integer.parseInt(t), X,Matriz,inOut[1], arge, argv, argr);
                    modo_nao_interativo_graficos(nomeespecie,formato,vetor_dimensao(Integer.parseInt(t),X,Matriz),vetor_taxa_de_variacao(Integer.parseInt(t),X,Matriz),matriz_distribuicao_populacao(Integer.parseInt(t),X,Matriz),matriz_distribuicao_populacao_normalizada(Integer.parseInt(t),X,Matriz),Integer.parseInt(t));
                }
            }
            else
            {
                System.out.println("Erro na sequência de dados introduzida no ficheiro.");
            }
        }
    }

    public static String getSingleValueArgs(char opt, String[] args) throws IndexOutOfBoundsException{
        // -t xxx , -g X
        String tValue = "";
        for (int i = 0; i < args.length; i++) {
            char c = isOption(args[i]);
            if (c==opt) {
                tValue=args[i+1];
                break;
            }
        }
        return tValue;
    }
    public static boolean getBoolValueArgs(char opt, String[] args){
        boolean value = false;
        for (int i = 0; i < args.length; i++) {
            char c = isOption(args[i]);
            if (c!='0' && c==opt) value=true;
        }
        return value;
    }
    public static String[] getLastTwoArgs(String[] args) throws ArrayIndexOutOfBoundsException{
        String[] rt = {"", ""};
        if (args.length>=2){
            rt[0] = args[args.length-2];
            rt[1] = args[args.length-1];
        }
        return rt;
        // [in, out]
    }
    public static char isOption(String arg){

        char c = arg.charAt(0);
        if (c=='-') return arg.charAt(1);
        else return '0';
    }
    public static boolean verification(char[] char_X, char[] char_S,char[] char_F){
        boolean bool = true;
        int a=0,pos_x,pos_f,pos_s,i_x=1,i_f=1,i_s=1;
        for (int j = 0; j < char_X.length; j++) {
            if (Integer.parseInt(String.valueOf(char_X[2])) != 0) {
                a++;
            }
            if (String.valueOf(char_X[j]).equals(",")) {
                pos_x=j;
                if (Integer.parseInt(String.valueOf(char_X[pos_x+4])) != i_x) {
                    a++;
                }
                else{
                    i_x++;
                }
            }
        }
        for (int j = 0; j < char_S.length; j++) {
            if (Integer.parseInt(String.valueOf(char_S[1])) != 0) {
                a++;
            }
            if (String.valueOf(char_S[j]).equals(",")) {
                pos_s=j;
                if (Integer.parseInt(String.valueOf(char_S[pos_s+3])) != i_s) {
                    a++;
                }
                else{
                    i_s++;
                }

            }
        }
        for (int j = 0; j < char_F.length; j++) {
            if (Integer.parseInt(String.valueOf(char_F[1])) != 0) {
                a++;
            }
            if (String.valueOf(char_F[j]).equals(",")) {
                pos_f=j;
                if (Integer.parseInt(String.valueOf(char_F[pos_f+3])) != i_f) {
                    a++;
                }
                else{
                    i_f++;
                }

            }
        }
        if(a>0)bool=false;
        return bool;
    }
    public static double[][]  matriz(int n){
        int coluna,linha;
        double[][] matriz = new double[n][n];
        for (coluna=0; coluna <n;coluna++){
            System.out.print("Digite a taxa média de fertilidade na " + (coluna+1) +"ª faixa etária: ");
            matriz[0][coluna]=ler.nextDouble();
        }
        for(linha=1;linha<n;linha++){
            System.out.print("Digite a taxa média de sobreviventes na " + (linha)+"ª faixa etária: ");
            matriz[linha][linha-1]= ler.nextDouble();
        }
        return matriz;
    }
    public static int[] vetor_X(int n){

        int[] vetor_X = new int[n];
        for(int i=0;i<n;i++){
            System.out.print("Quantidade de elementos da espécie na "+(i+1)+"ª faixa etária: ");
            vetor_X[i]=ler.nextInt();
        }
        return vetor_X;
    }
    public static char[] read_char_X() throws FileNotFoundException {
        Scanner scan = new Scanner(new File(path));
        char[] char_a,char_b,char_c;
        String a=scan.nextLine();
        char_a=a.toCharArray();
        String b=scan.nextLine();
        char_b=b.toCharArray();
        String c=scan.nextLine();
        char_c=c.toCharArray();
        if(String.valueOf(char_a[0]).equals("x")){
            return  char_a;
        }
        else if(String.valueOf(char_b[0]).equals("x")){
            return  char_b;
        }
        else if(String.valueOf(char_c[0]).equals("x")){
            return  char_c;
        }
        else{
            return  char_a;
        }
    }
    public static char[] read_char_S() throws FileNotFoundException {
        Scanner scan = new Scanner(new File(path));
        char[] char_a,char_b,char_c;
        String a=scan.nextLine();
        char_a=a.toCharArray();
        String b=scan.nextLine();
        char_b=b.toCharArray();
        String c=scan.nextLine();
        char_c=c.toCharArray();
        if(String.valueOf(char_a[0]).equals("s")){
            return  char_a;
        }
        else if(String.valueOf(char_b[0]).equals("s")){
            return  char_b;
        }
        else if(String.valueOf(char_c[0]).equals("s")){
            return  char_c;
        }
        else{
            return  char_a;
        }
    }
    public static char[] read_char_F() throws FileNotFoundException {
        Scanner scan = new Scanner(new File(path));
        char[] char_a,char_b,char_c;
        String a=scan.nextLine();
        char_a=a.toCharArray();
        String b=scan.nextLine();
        char_b=b.toCharArray();
        String c=scan.nextLine();
        char_c=c.toCharArray();
        if(String.valueOf(char_a[0]).equals("f")){
            return  char_a;
        }
        else if(String.valueOf(char_b[0]).equals("f")){
            return  char_b;
        }
        else if(String.valueOf(char_c[0]).equals("f")){
            return  char_c;
        }
        else{
            return  char_a;
        }
    }
    public static int[] fill_X(char[] char_X) {
        int a = 0, b = 0, i = 0, cont = 0,cont2=0;
        for (int j = 0; j < char_X.length; j++) {
            if (String.valueOf(char_X[j]).equals("=")) {
                cont++;
            }
        }
        int[] X = new int[cont];
        String c, n = "";
        for (int j = 0; j < char_X.length; j++) {
            if (String.valueOf(char_X[j]).equals("=")) {
                a = j + 1;
                cont2++;
            }
            if (String.valueOf(char_X[j]).equals(",")){
                b = j;
            }
            else if(cont2==cont){
                cont2++;
                b=char_X.length;
            }
            if (a < b) {
                for (; a < b; a++) {
                    c = String.valueOf(char_X[a]);
                    n = n + c;
                }
                X[i] = Integer.parseInt(n);
                n = "";
                i++;
            }
        }
        return X;
    }
    public static double[][] fill_S_F(char[] S,char[] F){
        int a = 0, b = 0, i = 0, cont = 0,cont2=0;
        for (int j = 0; j < F.length; j++) {
            if (String.valueOf(F[j]).equals("=")) {
                cont++;
            }
        }
        double[][] matriz = new double[cont][cont];
        String c, n = "";
        for (int j = 0; j < F.length; j++) {
            if (String.valueOf(F[j]).equals("=")) {
                a = j + 1;
                cont2++;
            }
            if (String.valueOf(F[j]).equals(",")){
                b = j;
            }
            else if(cont2==cont){
                cont2++;
                b=F.length;
            }
            if (a < b) {
                for (; a < b; a++) {
                    c = String.valueOf(F[a]);
                    n = n + c;
                }
                matriz[0][i] = Double.parseDouble(n);
                n = "";
                i++;
            }
        }
        b=0;
        a=0;
        cont=0;
        cont2=0;
        n = "";
        int linha=1;
        for (int j = 0; j < S.length; j++) {
            if (String.valueOf(S[j]).equals("=")) {
                cont++;
            }
        }
        for (int j = 0; j < S.length; j++) {
            if (String.valueOf(S[j]).equals("=")) {
                a = j + 1;
                cont2++;
            }
            if (String.valueOf(S[j]).equals(",")){
                b = j;
            }
            else if(cont2==cont){
                cont2++;
                b=S.length;
            }
            if (a < b) {
                for (; a < b; a++) {
                    c = String.valueOf(S[a]);
                    n = n + c;
                }
                matriz[linha][linha-1] = Double.parseDouble(n);
                linha++;
                n = "";
                i++;
            }
        }
        return matriz;
    }
    public static void menu(int[] populacao_inicial, double[][] fatores, String nomeespecie) throws IOException {
        String resultado;

        int comando;
        int geracoes;
        System.out.println("Para quantas gerações deseja estimar?");
        geracoes = ler.nextInt();
            do {
                System.out.println("Insira uma opção entre as seguintes:");
                System.out.println("(1) Distribuição da população para cada instante");
                System.out.println("(2) Distribuição normalizada da população para cada instante");
                System.out.println("(3) Dimensão da população num determinado instante");
                System.out.println("(4) Taxa de variação da população");
                System.out.println("(5) Analisar o comportamento assintótico");
                System.out.println("(6) Representações gráficas");
                System.out.println("(7) Mudar o número de gerações a estimar");
                System.out.println("(8) Mudar a população inicial e as taxas de fertilidade e sobrevivência");
                System.out.println("(9) Sair");
                comando=ler.nextInt();

                switch (comando) {
                    case 1:
                        resultado= distribuicao_da_populacao(geracoes, populacao_inicial, fatores);
                        System.out.println(resultado);
                        break;
                    case 2:
                        resultado= distribuicao_da_populacao_normalizado(geracoes, populacao_inicial, fatores);
                        System.out.println(resultado);
                        break;
                    case 3:
                        System.out.println("Para que momento deseja calcular a dimensão da população?");
                        int instante = ler.nextInt();
                        resultado=dimensao_da_populacao_por_instante(instante, populacao_inicial, fatores);
                        System.out.println(resultado);
                        break;
                    case 4:
                        resultado=taxa_de_variacao(geracoes, populacao_inicial, fatores);
                        System.out.println(resultado);
                        break;
                    case 5:
                        analiseComportamentoAssintotico(fatores);
                        break;
                    case 6:
                        do{
                            System.out.println("(1) Representação gráfica da dimensão da população");
                            System.out.println("(2) Representação gráfica da taxa de variação");
                            System.out.println("(3) Representação gráfica da distribuição da população");
                            System.out.println("(4) Representação gráfica da distribuição normalizada da população");
                            comando = ler.nextInt();
                            switch (comando) {
                                case 1:
                                    grafico_dimensao(vetor_dimensao(geracoes,populacao_inicial,fatores),nomeespecie);
                                    break;
                                case 2:
                                    grafico_taxa_variacao(vetor_taxa_de_variacao(geracoes, populacao_inicial, fatores),nomeespecie);
                                    break;
                                case 3:
                                    grafico_distribuicao(matriz_distribuicao_populacao(geracoes,populacao_inicial,fatores),geracoes,nomeespecie);
                                    break;
                                    case 4:
                                        grafico_distribuicao_normalizada(matriz_distribuicao_populacao_normalizada(geracoes,populacao_inicial,fatores),geracoes,nomeespecie);
                                        break;
                                default:
                                    System.out.println("Digite uma opção válida.");
                            }
                        }while(comando!=1 && comando!=2 && comando!=3 && comando!=4);
                        break;
                    case 7:
                        System.out.println("Para quantas gerações deseja estimar?");
                        geracoes= ler.nextInt();
                        break;
                    case 8:
                        System.out.println("Digite a quantidade de faixas etárias para a análise da espécie:");
                        int N = ler.nextInt();
                        populacao_inicial = vetor_X(N);
                        fatores = matriz(N);
                        break;
                    case 9:
                        break;
                    default:
                        System.out.println("Digite uma opção válida");
                }

            }while(comando!=9);
    }

    public static void modo_nao_interativo(int geracoes, int[] populacao, double [][] fatores, String ficheiro_saida, boolean arge, boolean argv, boolean argr) throws IOException {

        String texto = "Gerações a estimar: "+geracoes;
        texto+= System.lineSeparator() + System.lineSeparator();
        texto+=escrever_matriz(fatores);
        texto+=escrever_vetor(populacao);
        texto+= "Distribuição da população (não normalizado):"+System.lineSeparator()+distribuicao_da_populacao(geracoes,populacao,fatores);
        texto+= System.lineSeparator();
        texto+= "Distribuição da população (normalizado):"+System.lineSeparator()+distribuicao_da_populacao_normalizado(geracoes,populacao,fatores);
        texto+= System.lineSeparator();
        if(argv) {
            texto += "Dimensão da população: " + System.lineSeparator() + dimensao_da_populacao(geracoes, populacao, fatores);
        }
        texto+= System.lineSeparator();
        if(argr) {
            texto += "Taxa de variação da população:" + System.lineSeparator() + taxa_de_variacao(geracoes, populacao, fatores);
        }
        texto+= System.lineSeparator();
        if(arge) {
            //texto+= "Maior valor próprio e vetor associado"+System.lineSeparator()+
        }
        output_ficheiro(texto,ficheiro_saida);

        System.out.println("Ficheiro criado com sucesso.");
    }

    public static void modo_nao_interativo_graficos(String nomeespecie, String formato, double[] dimensao_instante, double[] taxa_instante, double[][] distribuicao_populacao, double[][] distribuicao_normalizada, int geracoes) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy");
        LocalDateTime now = LocalDateTime.now();

        preenchimento_ficheiro_grafico_vetor("dimensao.txt", dimensao_instante);
        guardar_grafico_vetor("grafico_dimensao.gp",formato,"dimensao.txt",nomeespecie + " grafico dimensao da populacao " + dtf.format(now),"Dimensão da população em cada instante","Instante","Dimensão da população");

        preenchimento_ficheiro_grafico_vetor("taxa_variacao.txt", taxa_instante);
        guardar_grafico_vetor("grafico_taxa.gp",formato,"taxa_variacao.txt",nomeespecie + " grafico taxa de variacao da populacao " + dtf.format(now),"Taxa de variação em cada instante","Instante","Taxa de variação");

        preenchimento_ficheiro_grafico_distribuicao("distribuicao.txt", distribuicao_populacao, geracoes);
        guardar_grafico_matriz(distribuicao_populacao,"grafico_distribuicao.gp",formato,"distribuicao.txt",nomeespecie + " grafico distribuicao da populacao " + dtf.format(now),"Distribuição da população em cada instante","Instante","Quantidade de elementos");

        preenchimento_ficheiro_grafico_distribuicao("distribuicao_normalizada.txt", distribuicao_normalizada, geracoes);
        guardar_grafico_matriz(distribuicao_normalizada,"grafico_distribuicao_normalizada.gp",formato,"distribuicao_normalizada.txt",nomeespecie + " grafico distribuicao normalizada da populacao " + dtf.format(now),"Distribuição normalizada da população em cada instante","Instante","Percentagem");

    }

    public static String escrever_matriz(double[][] matriz)
    {
        String str="Matriz de Leslie:"+System.lineSeparator();
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz.length; j++) {
                str+= matriz[i][j] + " ";
            }
            str+= System.lineSeparator();
        }
        str+= System.lineSeparator();
        return str;
    }

    public static String escrever_vetor(int[] vetor) {

        String str="Número de indivíduos iniciais por etapa:"+System.lineSeparator();
        for (int i = 0; i < vetor.length; i++) {
            str+= (i+1)+"ª etapa: "+vetor[i];
            str+= System.lineSeparator();
        }
        str+= System.lineSeparator();
        return str;

    }
    public static void output_ficheiro(String conteudo, String nomeficheiro) throws IOException {
        FileWriter fstream = new FileWriter(nomeficheiro,true);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(conteudo +"\n");
        out.close();
    }
    public static String distribuicao_da_populacao_normalizado(int instante, int[] populacaoinicial, double[][] fatores)
    {

        String retorno="";
        for(int i=0; i<=instante;i++) {

            retorno += "Geração "+i+": ";
            retorno += distribuicao_da_populacao_por_instante_normalizado(i, populacaoinicial, fatores);
            retorno += System.lineSeparator();
        }
        return retorno;
    }

    public static String distribuicao_da_populacao(int instante, int[] populacaoinicial, double[][] fatores)
    {

        String retorno="";
        for(int i=0; i<=instante;i++) {

            retorno += "Geração "+i+": ";
            retorno += distribuicao_da_populacao_por_instante(i, populacaoinicial, fatores);
            retorno += System.lineSeparator();
        }
        return retorno;
    }


    public static String distribuicao_da_populacao_por_instante_normalizado(int instante, int[] populacaoinicial, double[][] fatores)
    {

        double[] populacaoporinstantearray = array_de_populacao_por_instante(instante, populacaoinicial, fatores);
        double populacaototal = total_da_populacao(populacaoporinstantearray);
        double percentagempopulacao;
        String retorno= "";


        for(int i=0; i<populacaoporinstantearray.length;i++) {
            if(populacaototal!=0)
            percentagempopulacao = (populacaoporinstantearray[i]/populacaototal)*100;
            else
                percentagempopulacao=0;
            retorno += (i+1) + "ª faixa etária: " + String.format("%.2f", percentagempopulacao) + " ";

        }

        return retorno;
    }

    public static String distribuicao_da_populacao_por_instante(int instante, int[] populacaoinicial, double[][] fatores)
    {

        double[] populacaoporinstantearray = array_de_populacao_por_instante(instante, populacaoinicial, fatores);
        String retorno= "";

        for(int i=0; i<populacaoporinstantearray.length;i++) {

            retorno += (i+1) + "ª faixa etária: " + String.format("%.2f", populacaoporinstantearray[i])+" ";

        }

        return retorno;
    }

    public static String taxa_de_variacao(int instante, int[] populacaoinicial, double[][] fatores)
    {

        String retorno="";
        for(int i=0; i<instante;i++) {

            retorno += taxa_de_variacao_por_instante(i, populacaoinicial, fatores);
            retorno += System.lineSeparator();
        }
        return retorno;
    }

    public static String taxa_de_variacao_por_instante(int instante, int [] populacaoinicial, double[][] fatores)
    {
        String taxa="";


        taxa+= "Geração "+instante+" para "+(instante+1)+": "+String.format("%.2f", calcular_taxa(instante+1,populacaoinicial,fatores));

        return taxa;
    }

    public static double calcular_taxa(int instante, int [] populacaoinicial, double[][] fatores)
    {
        double populacaototal= total_da_populacao(array_de_populacao_por_instante(instante, populacaoinicial, fatores));
        double populacaoanterior = total_da_populacao(array_de_populacao_por_instante(instante-1, populacaoinicial, fatores));
        double resultado;
        if(populacaoanterior==0)
            resultado=0;
        else
            resultado=populacaototal/populacaoanterior;
        return resultado;
    }

    public static boolean test_calcular_taxa(int instante, int[] populacaoinicial, double[][] fatores, double resultadoesperado)
    {

        double calculo= Math.round(calcular_taxa(instante, populacaoinicial, fatores)*100.0)/100.0;

        return calculo == resultadoesperado;
    }

    public static String dimensao_da_populacao(int instante, int[] populacaoinicial, double[][] fatores)
    {

        String retorno="";
        for(int i=0; i<=instante;i++) {

            retorno += dimensao_da_populacao_por_instante(i, populacaoinicial, fatores);
            retorno += System.lineSeparator();
        }
        return retorno;
    }

    public static String dimensao_da_populacao_por_instante(int instante, int [] populacaoinicial, double[][] fatores)
    {

        double populacaototal = total_da_populacao(array_de_populacao_por_instante(instante, populacaoinicial, fatores));

        return "Geração " + instante + ": " + String.format("%.2f", populacaototal);
    }

    public static double total_da_populacao(double[] populacaoarray)
    {
        double populacaototal=0;
        for(int i=0; i<populacaoarray.length; i++)
        {
            populacaototal = populacaototal+ populacaoarray[i];
        }

        return populacaototal;
    }

    public static boolean test_total_da_populacao(double[] populacaoarray, double resultado)
    {
        double calculo= Math.round(total_da_populacao(populacaoarray)*100.0)/100.0;

        return calculo == resultado;
    }
    public static double[] array_de_populacao_por_instante(int instante, int[] populacaoinicial, double[][] fatores)
    {

        double[] populacao_instante= new double[populacaoinicial.length], populacao_instante2 = new double[populacaoinicial.length];
        for(int i=0; i<populacao_instante.length;i++)
        {
            populacao_instante[i] = populacaoinicial[i];
        }
        //Se escrever de outra forma o populacao_instante, como por exemplo "populacao_instante = populacaoinicial",
        //o vetor original também será alterado
        double soma;
        double multiplicacao;
        for(int i=1; i<=instante;i++)
        {
            for(int l=0; l<populacao_instante.length;l++)
            {
                populacao_instante2[l] = populacao_instante[l];
            }

            for(int j=0; j<populacaoinicial.length;j++)
            {

                soma=0;
                for(int k=0; k<fatores.length;k++)
                {
                    multiplicacao= fatores[j][k] * populacao_instante2[k];

                    soma += multiplicacao;
                }

                populacao_instante[j]= soma;
            }
        }

        return populacao_instante;
    }

    public static boolean test_array_de_populacao_por_instante(int instante, int[] populacaoinicial, double[][] fatores, double[] resultado)
    {
        boolean teste= true;
        double[] calculo = array_de_populacao_por_instante(instante, populacaoinicial, fatores);

        for (int i = 0; i < calculo.length; i++) {
            if(calculo[i]!=resultado[i])
            {
                teste=false;
            }
        }

        return teste;
    }

    public static double[][] matriz_distribuicao_populacao(int instante, int[] populacaoinicial, double[][] fatores ) {
        double[] populacao_instante;
        double[][] matriz_populacao = new double[populacaoinicial.length][instante+1];

        for (int i = 0; i <= instante; i++) {
            populacao_instante = array_de_populacao_por_instante(i, populacaoinicial, fatores);
            for (int j = 0; j < populacaoinicial.length; j++) {
                matriz_populacao[j][i] = populacao_instante[j];
            }
        }
        return matriz_populacao;
    }

    public static double[][] matriz_distribuicao_populacao_normalizada(int instante, int[] populacaoinicial, double[][] fatores ) {
        double[] populacao_instante;
        double[][] matriz_populacao = new double[populacaoinicial.length][instante+1];

        for (int i = 0; i <= instante; i++) {
            populacao_instante = array_de_populacao_por_instante(i, populacaoinicial, fatores);
            for (int j = 0; j < populacaoinicial.length; j++) {
                matriz_populacao[j][i] = Math.round((populacao_instante[j]/total_da_populacao(populacao_instante))* 100.0);
            }
        }
        return matriz_populacao;
    }

    public static double[] vetor_taxa_de_variacao(int instante, int[] populacaoinicial, double[][] fatores ) {
        double[] vetor_taxas = new double[instante];


        for (int i = 0; i < vetor_taxas.length; i++) {
            vetor_taxas[i]= Math.round(calcular_taxa(i+1,populacaoinicial,fatores) * 100.0) / 100.0;
        }
        return vetor_taxas;
    }

    public static double[] vetor_dimensao(int instante, int[] populacaoinicial, double[][] fatores ) {
        double[] vetor_dimensao = new double[instante+1];


        for (int i = 0; i < vetor_dimensao.length; i++) {
            vetor_dimensao[i]= total_da_populacao(array_de_populacao_por_instante(i, populacaoinicial, fatores));
        }
        return vetor_dimensao;
    }

    public static void analiseComportamentoAssintotico(double[][] fatores) {
        double eigenValue = MaiorValorProprio(fatores);
        System.out.println(eigenValue);

        try {
            double[] eigenVector = VetorProprio(eigenValue, fatores);
            System.out.println(eigenVector.length);
        } catch (IllegalArgumentException e ){
            System.out.println("Ocorreu um erro no calculo do vetor proprio \n");
        }
    }
    public static double MaiorValorProprio(double[][] populacao) {
        org.la4j.Matrix m = new org.la4j.matrix.dense.Basic2DMatrix(populacao);

        org.la4j.decomposition.EigenDecompositor decomp = new org.la4j.decomposition.EigenDecompositor(m);
        org.la4j.Matrix[] eigen = decomp.decompose();

        double maior = 0;
        org.la4j.Matrix dou = eigen[1].toDenseMatrix();

        for (int j = 0; j < dou.columns(); j++) {
            for (int k = 0; k < dou.rows(); k++) {
                double v = dou.get(j,k);
                if (v > maior) maior = v;
            }
        }
        return maior;
    }
    public static double[] VetorProprio(double eigV, double[][] matriz) throws IllegalArgumentException {
        Matrix m = new Basic2DMatrix(matriz);

        Matrix t = m.subtract(Matrix.identity(m.columns()).multiply(eigV));

        double[] zerVecTmp = new double[m.columns()];
        for (int i = 0; i < m.columns(); i++) {
            zerVecTmp[i]=0;
        }
        org.la4j.Vector zer = new BasicVector(zerVecTmp);

        GaussianSolver solver = new GaussianSolver(t);
        org.la4j.Vector res = solver.solve(zer);

        System.out.println(res);

        return (res.toDenseVector().toArray());
    }

    public static void grafico_dimensao(double[] dimensao_instante, String nomeespecie) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy");
        LocalDateTime now = LocalDateTime.now();
        String output = nomeespecie + " grafico dimensao da populacao " + dtf.format(now);

        File dimensao = new File("dimensao.txt");
        preenchimento_ficheiro_grafico_vetor("dimensao.txt", dimensao_instante);

        File executavel = new File("grafico_dimensao.gp");
        FileWriter outF = new FileWriter("grafico_dimensao.gp");
        PrintWriter out = new PrintWriter(outF);


        out.print("set title " + "\"Dimensao da populacao em cada instante\"" + "\n");
        out.print("set xlabel " + "\"Instante\"" + "\n");
        out.print("set ylabel " + "\"Dimensao da populacao\"" + "\n");
        out.println("plot \"dimensao.txt\" w lp notitle");
        out.close();
        Runtime.getRuntime().exec("gnuplot -p grafico_dimensao.gp");

        int guardar;
        do {
            System.out.println("Deseja guardar o gráfico?");
            System.out.println("(1) Sim");
            System.out.println("(2) Não");
            guardar = ler.nextInt();
            if(guardar!=1 && guardar!=2) System.out.println("Digite uma opção válida");
        }while (guardar!=1 && guardar!=2);
        executavel.delete();

        if(guardar==1){
            System.out.println("Qual o formato desejado para o gráfico?");
            String formato="";
            int opcao;
            do{
                System.out.println("(1) png");
                System.out.println("(2) txt");
                System.out.println("(3) eps");
                opcao = ler.nextInt();
                switch (opcao){
                    case 1:
                        formato="png";
                        break;
                    case 2:
                        formato="txt";
                        break;
                    case 3:
                        formato="eps";
                        break;
                    default:
                        System.out.println("Digite uma opção válida");
                }
            }while(opcao!= 1 && opcao!=2 && opcao!=3);
            guardar_grafico_vetor("grafico_dimensao.gp",formato,"dimensao.txt",output,"Dimensão da população em cada instante","Instante","Dimensão da população");
        }else dimensao.delete();
    }

    public static void preenchimento_ficheiro_grafico_vetor(String nome_ficheiro, double[] vetor) throws IOException {
        File dados = new File("C:\\Users\\danie\\OneDrive\\Documents\\LAPR1_projeto\\source\\" + nome_ficheiro);
        PrintWriter out = new PrintWriter(dados);
        for (int linha=0;linha<vetor.length;linha++){
            out.println(linha + " " + vetor[linha]);
        }
        out.close();
    }

    public static void grafico_taxa_variacao(double[] taxa_instante, String nomeespecie) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy");
        LocalDateTime now = LocalDateTime.now();
        String output = nomeespecie + " grafico taxa de variacao da populacao " + dtf.format(now);

        File taxa = new File("taxa_variacao.txt");
        preenchimento_ficheiro_grafico_vetor("taxa_variacao.txt",taxa_instante);

        File executavel = new File("grafico_taxa.gp");
        FileWriter outF = new FileWriter("grafico_taxa.gp");
        PrintWriter out = new PrintWriter(outF);

        out.print("set title " + "\"Taxa de variacao em cada instante\"" + "\n");
        out.print("set xlabel " + "\"Instante\"" + "\n");
        out.print("set ylabel " + "\"Taxa de variacao\"" + "\n");
        out.println("plot \"taxa_variacao.txt\" w lp notitle");
        out.close();
        Runtime.getRuntime().exec("gnuplot -p grafico_taxa.gp");

        int guardar;
        do {
            System.out.println("Deseja guardar o gráfico?");
            System.out.println("(1) Sim");
            System.out.println("(2) Não");
            guardar = ler.nextInt();
            if(guardar!=1 && guardar!=2) System.out.println("Digite uma opção válida");
        }while (guardar!=1 && guardar!=2);
        executavel.delete();

        if(guardar==1){
            System.out.println("Qual o formato desejado para o gráfico?");
            String formato="";
            int opcao;
            do{
                System.out.println("(1) png");
                System.out.println("(2) txt");
                System.out.println("(3) eps");
                opcao = ler.nextInt();
                switch (opcao){
                    case 1:
                        formato="png";
                        break;
                    case 2:
                        formato="txt";
                        break;
                    case 3:
                        formato="eps";
                        break;
                    default:
                        System.out.println("Digite uma opção válida");
                }
            }while(opcao!= 1 && opcao!=2 && opcao!=3);
            guardar_grafico_vetor("grafico_taxa.gp",formato,"taxa_variacao.txt",output,"Taxa de variação em cada instante","Instante","Taxa de variação");
        } else taxa.delete();
    }

    public static void guardar_grafico_vetor(String nome_ficheiro,String formato, String dados, String output, String title, String xlabel, String ylabel) throws IOException {
        File executavel = new File("C:\\Users\\danie\\OneDrive\\Documents\\LAPR1_projeto\\source\\" + nome_ficheiro);
        PrintWriter out = new PrintWriter(executavel);

        out.println("cd \"C:\\\\Users\\\\danie\\\\OneDrive\\\\Documents\\\\LAPR1_projeto\\\\source\"");
        if(formato.equals("png")){
            out.println("set terminal png");
        }else if(formato.equals("txt")){
            out.println("set terminal dumb");
        }else out.println("set terminal postscript");
        out.println("set output \""+ output + "." + formato + "\"");
        out.print("set title " + "\"" + title + "\"" + "\n");
        out.print("set xlabel " + "\"" + xlabel + "\"" + "\n");
        out.print("set ylabel " + "\"" + ylabel + "\"" + "\n");
        out.print("plot \"" + dados + "\" w lp notitle");
        out.close();
        Runtime.getRuntime().exec("gnuplot C:\\\\Users\\\\danie\\\\OneDrive\\\\Documents\\\\LAPR1_projeto\\\\source\\\\" + nome_ficheiro);
    }

    public static void grafico_distribuicao(double[][] distribuicao_populacao, int geracoes, String nomeespecie) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy");
        LocalDateTime now = LocalDateTime.now();
        String output = nomeespecie + " grafico distribuicao da populacao " + dtf.format(now);

        File distribuicao = new File("distribuicao.txt");
        preenchimento_ficheiro_grafico_distribuicao("distribuicao.txt", distribuicao_populacao, geracoes);

        File executavel = new File("grafico_distribuicao.gp");
        FileWriter outF = new FileWriter("grafico_distribuicao.gp");
        PrintWriter out = new PrintWriter(outF);

        out.print("set title " + "\"Distribuicao da populacao em cada instante\"" + "\n");
        out.print("set xlabel " + "\"Instante\"" + "\n");
        out.print("set ylabel " + "\"Quantidade de elementos\"" + "\n");
        out.print("plot ");
        for(int i=1;i<=distribuicao_populacao.length;i++){
            out.print("\"distribuicao.txt\" u 1:" + (i+1) + " w lp t \"Faixa etaria " + i + "\", ");
        }
        out.close();
        Runtime.getRuntime().exec("gnuplot -p grafico_distribuicao.gp");

        int guardar;
        do {
            System.out.println("Deseja guardar o gráfico?");
            System.out.println("(1) Sim");
            System.out.println("(2) Não");
            guardar = ler.nextInt();
            if(guardar!=1 && guardar!=2) System.out.println("Digite uma opção válida");
        }while (guardar!=1 && guardar!=2);
        executavel.delete();

        if(guardar==1){
            System.out.println("Qual o formato desejado para o gráfico?");
            String formato="";
            int opcao;
            do{
                System.out.println("(1) png");
                System.out.println("(2) txt");
                System.out.println("(3) eps");
                opcao = ler.nextInt();
                switch (opcao){
                    case 1:
                        formato="png";
                        break;
                    case 2:
                        formato="txt";
                        break;
                    case 3:
                        formato="eps";
                        break;
                    default:
                        System.out.println("Digite uma opção válida");
                }
            }while(opcao!= 1 && opcao!=2 && opcao!=3);
            guardar_grafico_matriz(distribuicao_populacao,"grafico_distribuicao.gp",formato,"distribuicao.txt",output,"Distribuição da população em cada instante","Instante","Quantidade de elementos");
        }else distribuicao.delete();
    }

    public static void preenchimento_ficheiro_grafico_distribuicao(String nome_ficheiro, double[][] matriz, int geracoes) throws IOException {
        File dados = new File("C:\\Users\\danie\\OneDrive\\Documents\\LAPR1_projeto\\source\\" + nome_ficheiro);
        PrintWriter out = new PrintWriter(dados);
        for (int coluna=0;coluna<=geracoes;coluna++){
            out.print(coluna + " ");
            for (int linha=0;linha<matriz.length;linha++ ){
                out.print(matriz[linha][coluna] + " ");
            }
            out.println();
        }
        out.close();
    }

    public static void grafico_distribuicao_normalizada(double[][] distribuicao_normalizada, int geracoes, String nomeespecie) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy");
        LocalDateTime now = LocalDateTime.now();
        String output = nomeespecie + " grafico distribuicao normalizada da populacao " + dtf.format(now);

        File distribuicaonormalizada = new File("distribuicao_normalizada.txt");
        preenchimento_ficheiro_grafico_distribuicao("distribuicao_normalizada.txt", distribuicao_normalizada, geracoes);

        File executavel = new File("grafico_distribuicao_normalizada.gp");
        FileWriter outF = new FileWriter("grafico_distribuicao_normalizada.gp");
        PrintWriter out = new PrintWriter(outF);

        out.print("set title " + "\"Distribuicao normalizada da populacao em cada instante\"" + "\n");
        out.print("set xlabel " + "\"Instante\"" + "\n");
        out.print("set ylabel " + "\"Percentagem\"" + "\n");
        out.print("plot ");
        for(int i=1;i<=distribuicao_normalizada.length;i++){
            out.print("\"distribuicao_normalizada.txt\" u 1:" + (i+1) + " w lp t \"Faixa etaria " + i + "\", ");
        }
        out.close();
        Runtime.getRuntime().exec("gnuplot -p grafico_distribuicao_normalizada.gp");

        int guardar;
        do {
            System.out.println("Deseja guardar o gráfico?");
            System.out.println("(1) Sim");
            System.out.println("(2) Não");
            guardar = ler.nextInt();
            if(guardar!=1 && guardar!=2) System.out.println("Digite uma opção válida");
        }while (guardar!=1 && guardar!=2);
        executavel.delete();

        if(guardar==1){
            System.out.println("Qual o formato desejado para o gráfico?");
            String formato="";
            int opcao;
            do{
                System.out.println("(1) png");
                System.out.println("(2) txt");
                System.out.println("(3) eps");
                opcao = ler.nextInt();
                switch (opcao){
                    case 1:
                        formato="png";
                        break;
                    case 2:
                        formato="txt";
                        break;
                    case 3:
                        formato="eps";
                        break;
                    default:
                        System.out.println("Digite uma opção válida");
                }
            }while(opcao!= 1 && opcao!=2 && opcao!=3);
            guardar_grafico_matriz(distribuicao_normalizada,"grafico_distribuicao_normalizada.gp",formato,"distribuicao_normalizada.txt",output,"Distribuição normalizada da população em cada instante","Instante","Percentagem");
        }else distribuicaonormalizada.delete();
    }

    public static void guardar_grafico_matriz(double[][] matriz,String nome_ficheiro,String formato, String dados, String output, String title, String xlabel, String ylabel) throws IOException {
        File executavel = new File("C:\\Users\\danie\\OneDrive\\Documents\\LAPR1_projeto\\source\\" + nome_ficheiro);
        PrintWriter out = new PrintWriter(executavel);

        out.println("cd \"C:\\\\Users\\\\danie\\\\OneDrive\\\\Documents\\\\LAPR1_projeto\\\\source\"");
        if(formato.equals("png")){
            out.println("set terminal png");
        }else if(formato.equals("txt")){
            out.println("set terminal dumb");
        }else out.println("set terminal postscript");
        out.println("set output \""+ output + "." + formato + "\"");
        out.print("set title " + "\"" + title + "\"" + "\n");
        out.print("set xlabel " + "\"" + xlabel + "\"" + "\n");
        out.print("set ylabel " + "\"" + ylabel + "\"" + "\n");
        out.print("plot ");
        for(int i=1;i<=matriz.length;i++){
            out.print("\"" + dados + "\" u 1:" + (i+1) + " w lp t \"Faixa etária " + i + "\", ");
        }
        out.close();
        Runtime.getRuntime().exec("gnuplot C:\\\\Users\\\\danie\\\\OneDrive\\\\Documents\\\\LAPR1_projeto\\\\source\\\\" + nome_ficheiro);
    }
}
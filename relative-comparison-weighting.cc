#include <iostream>
#include <string>
#include <vector>
#include <algorithm>
using namespace std;

// turns the user into a < operator for comparison sort
// this will segfault if you have inconsistent preferences
bool user_compare(string a, string b) {
    char c;
    do {
        cout << "[y/n] do you prefer " << a << " to " << b << "?" << endl;
        cin >> c;
    } while (c != 'y' && c != 'n');
    
    return c == 'y';
}

vector<float> instantaneous_rcw(const vector<float> &rels) {
    // given equations:
    // 1 = a + b + c + ...
    // r_ab * a = b
    // r_bc * b = c
    // ...
    
    // and variables r_ij (in rels), solve for a, b, c...
    // into vector results
    
    vector<float> results;
    
    // we can write first equation as 
    // 1 = (a) + (r_ab * a) + (r_ab * r_bc * a) + ...
    // so we will calculate the products of r_ab... here
    vector<float> products;
    
    // a = 1 / (1 + (r_ab) + (r_ab * r_bc) + ...)
    // sum is the sum of the terms in the denominator
    float sum = 1.f;
    products.push_back(1);
    for (size_t i = 0; i < rels.size(); ++i) {
        // compute next term of the product sequence by multiplying the
        // last one against r_ij
        products.push_back(products[i] * rels[i]);
        sum += products[i + 1];
    }
    
    // store a in results[0]
    results.push_back(1.f / sum);
    
    // now, each b, c, ... is computed by
    // example: b = 1 - a - c - ...
    // thus b = 1 - a - (r_ab * r_bc) * a - ...
    for (size_t i = 0; i < rels.size(); ++i) {
        float antisum = 1.f;
        for (size_t j = 0; j < products.size(); ++j) {
            // if calculating b, we don't want to subtract the b term
            if (i + 1 == j) continue;
            
            // antisum -= a * (r_ab * r_bc * ...)
            antisum -= results[0] * products[j];
        }
        
        // store computed rcw
        results.push_back(antisum);
    }
    
    return results;
}    

void print_rcw(const vector<string> &items, const vector<float> &rels) {
    // get rcws given relative weights between items
    vector<float> rcws = instantaneous_rcw(rels);
    
    for (size_t i = 0; i < items.size(); ++i) {
        cout << (i + 1) << ") < > " << items[i] << "\t= " << rcws[i] << endl;
    }
}

void calculate_rel(string a, string b, float &rel) {
    // ask questions to determine for what n you would choose
    // n * b > a
    // given that you would choose a > b
    
    // probably an open ended binary search
}

// begin the calculation of RCW
void get_rcw(vector<string> &items) {
    size_t n = items.size();
    
    // ask user to sort items based on qualitative preference
    sort(items.begin(), items.end(), &user_compare);

    // pick default relative values such that
    // each value makes up one h more than its immediate successor
    // for h = 1 / (n * (n + 1) / 2)    [the sum of the arithmetic sequence 1..n]
    
    // ie. given n = 3, this provides
    // a = 3/6, b = 2/6, c = 1/6
    
    // since r_ab * a = b, we can solve for r_ab as
    // r_ab = b / a
    // ie. r_ij = (i - 1) / i, from above computations for constraints on a, b, etc
    vector<float> rels;
    for (size_t i = 0; i < n - 1; ++i) {
        rels.push_back(static_cast<float>(n - i - 1) / (n - i));
    }
    
    // simulate the user explicitly determining relative weights
    // between two consecutive elements in the items array, over time

    // this is essentially asking the user to make one decision every time he
    // logs in.
    size_t idx = 0;
    do {
        cout << endl << endl;
        
        // show the current values of the rcw computation
        print_rcw(items, rels);
        
        // ask the user to rate how many idx+1 are equivalent to idx
        calculate_rel(items[idx], items[idx + 1], rels[idx]);
        ++idx;
    } while(idx < rels.size());
}

int main() {    
    vector<string> items;
    items.push_back("apples");
    items.push_back("bananas");
    items.push_back("carrots");
    
    get_rcw(items);
    
    return 0;
}
import {Component, OnInit} from "@angular/core";
import {FormGroup, FormControl, Validators} from "@angular/forms";
import {ProductService} from "../product.service";
import {Router} from "@angular/router";
import {Product} from "../../model/product";

@Component({
    selector: 'product-update-component',
    templateUrl: 'product-update.component.html'
})
export class ProductUpdateComponent implements OnInit{
    productForm: FormGroup;
    loading = false;

    constructor(private productService: ProductService, private router: Router) {
    }

    ngOnInit(): void {
        this.productForm = new FormGroup({
            name: new FormControl('', Validators.required),
            cost: new FormControl('', Validators.required),
            type: new FormControl('', Validators.required)
        });
    }

    onSubmit() {
        this.loading = true;
        this.productService.update(new Product(this.productForm.value.name, this.productForm.value.cost, this.productForm.value.type))
            .subscribe(result => {
                if (result === true) {
                    alert("Success!");
                    this.router.navigate(['/']);
                } else {
                    this.loading = false;
                }
            });
    }
}
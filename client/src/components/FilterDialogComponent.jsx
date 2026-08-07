import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
// import { Input } from "@/components/ui/input";
// import { Label } from "@/components/ui/label";

import React from "react";

const FilterDialogComponent = ({
  filterData,
  handleFilterSubmit,
  content,
  filterKey,
}) => {
  const { name, label, isActive } = filterData;

  return (
    <Dialog key={filterKey}>
      <DialogTrigger asChild>
        <Button variant={isActive ? "default" : "outline"}>{label}</Button>
      </DialogTrigger>

      <DialogContent className="sm:max-w-[425px]">
        <form
          onSubmit={(e) => handleFilterSubmit(e, name)}
          className="space-y-4"
        >
          <DialogHeader>
            <DialogTitle>{label} Filter</DialogTitle>
            <DialogDescription>
              Set filter for <strong>{label}</strong>.
            </DialogDescription>
          </DialogHeader>
          {content}
          <DialogFooter>
            <DialogClose asChild>
              <Button variant="outline">Cancel</Button>
            </DialogClose>
            <DialogClose asChild>
              <Button type="submit">Submit</Button>
            </DialogClose>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default FilterDialogComponent;
